package com.uber.driverservice.service;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.common.enums.*;
import com.uber.common.event.*;
import com.uber.driverservice.model.*;
import com.uber.driverservice.repository.*;
import com.uber.driverservice.strategy.DriverMatchingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service @Slf4j
public class DriverService {

    private final DriverRepository driverRepo;
    private final WorkHoursRepository workHoursRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DriverMatchingStrategy matchingStrategy;

    @Value("${app.driver.max-work-hours:8}")
    private int maxWorkHours;

    private static final String DRIVER_LOCATION_KEY = "driver:location:";

    public DriverService(DriverRepository driverRepo, WorkHoursRepository workHoursRepo,
                          KafkaTemplate<String, Object> kafkaTemplate, RedisTemplate<String, Object> redisTemplate,
                          @Qualifier("nearestDriver") DriverMatchingStrategy matchingStrategy) {
        this.driverRepo = driverRepo;
        this.workHoursRepo = workHoursRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.matchingStrategy = matchingStrategy;
    }

    @Transactional
    public DriverResponse createDriver(Long userId, CreateDriverRequest req) {
        Driver driver = Driver.builder().id(userId).email(req.getEmail())
                .name(req.getName()).surname(req.getSurname())
                .telephoneNumber(req.getTelephoneNumber()).address(req.getAddress())
                .vehicleModel(req.getVehicleModel()).licensePlate(req.getLicensePlate())
                .vehicleType(req.getVehicleType()).passengerSeats(req.getPassengerSeats())
                .babyTransport(req.isBabyTransport()).petTransport(req.isPetTransport())
                .status(DriverStatus.UNAVAILABLE).build();
        return toResponse(driverRepo.save(driver));
    }

    public DriverResponse getById(Long id) { 
        return toResponse(findById(id)); 
    }

    public PaginatedResponse<DriverResponse> getAll(Pageable pageable) {
        Page<Driver> page = driverRepo.findAll(pageable);
        return new PaginatedResponse<>((int) page.getTotalElements(),
                page.getContent().stream().map(this::toResponse).toList());
    }

    @Transactional
    public DriverResponse toggleOnline(Long driverId, boolean goOnline) {
        Driver driver = findById(driverId);
        if (goOnline) {
            if (hasExceededWorkHours(driverId))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exceeded " + maxWorkHours + "h work limit today");
            driver.setStatus(DriverStatus.AVAILABLE);
            workHoursRepo.save(WorkHours.builder().driverId(driverId).startTime(LocalDateTime.now()).build());
        } else {
            driver.setStatus(DriverStatus.UNAVAILABLE);
            workHoursRepo.findActiveByDriverId(driverId).ifPresent(wh -> {
                wh.setEndTime(LocalDateTime.now()); 
                workHoursRepo.save(wh);
            });
        }
        return toResponse(driverRepo.save(driver));
    }

    @Transactional
    public void updateLocation(Long driverId, double lat, double lng) {
        Driver driver = findById(driverId);
        driver.setCurrentLatitude(lat);
        driver.setCurrentLongitude(lng);
        driverRepo.save(driver);

        // Cache in Redis for fast lookups
        Map<String, Double> location = Map.of("lat", lat, "lng", lng);
        redisTemplate.opsForValue().set(DRIVER_LOCATION_KEY + driverId, location, 5, TimeUnit.MINUTES);

        // Publish location event for ride tracking
        kafkaTemplate.send("driver-location", String.valueOf(driverId),
                DriverLocationEvent.builder().driverId(driverId).latitude(lat).longitude(lng)
                        .timestamp(LocalDateTime.now()).build());
    }

    /**
     * Called by Kafka consumer when a ride is requested.
     * Uses Strategy Pattern to find the best matching driver.
     */
    @Transactional
    public void matchDriverForRide(RideRequestedEvent event) {
        log.info("Finding driver for ride {}", event.getRideId());

        List<Driver> available = driverRepo.findByStatusAndVehicleType(DriverStatus.AVAILABLE, event.getVehicleType());

        // Filter by transport requirements
        available = available.stream()
                .filter(d -> !event.isBabyTransport() || d.isBabyTransport())
                .filter(d -> !event.isPetTransport() || d.isPetTransport())
                .toList();

        Optional<Driver> bestDriver = matchingStrategy.findBestDriver(available, event);

        if (bestDriver.isPresent()) {
            Driver driver = bestDriver.get();
            driver.setStatus(DriverStatus.ON_RIDE);
            driverRepo.save(driver);

            DriverAssignedEvent assigned = DriverAssignedEvent.builder()
                    .rideId(event.getRideId()).driverId(driver.getId()).passengerId(event.getPassengerId())
                    .driverName(driver.getName() + " " + driver.getSurname())
                    .vehicleModel(driver.getVehicleModel()).licensePlate(driver.getLicensePlate())
                    .driverLatitude(driver.getCurrentLatitude() != null ? driver.getCurrentLatitude() : 0)
                    .driverLongitude(driver.getCurrentLongitude() != null ? driver.getCurrentLongitude() : 0)
                    .timestamp(LocalDateTime.now()).build();

            kafkaTemplate.send("driver-assigned", String.valueOf(event.getRideId()), assigned);
            log.info("Driver {} assigned to ride {}", driver.getId(), event.getRideId());
        } else {
            log.warn("No available driver found for ride {}", event.getRideId());
            // Publish a status change event indicating no driver found
            kafkaTemplate.send("ride-status-changed", String.valueOf(event.getRideId()),
                    RideStatusChangedEvent.builder().rideId(event.getRideId())
                            .passengerId(event.getPassengerId())
                            .previousStatus(RideStatus.PENDING).newStatus(RideStatus.REJECTED)
                            .reason("No available drivers").timestamp(LocalDateTime.now()).build());
        }
    }

    @Transactional
    public void releaseDriver(Long driverId) {
        Driver driver = findById(driverId);
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepo.save(driver);
    }

    public boolean hasExceededWorkHours(Long driverId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<WorkHours> today = workHoursRepo.findTodayByDriverId(driverId, startOfDay);
        long totalMinutes = today.stream().mapToLong(wh -> {
            LocalDateTime end = wh.getEndTime() != null ? wh.getEndTime() : LocalDateTime.now();
            return Duration.between(wh.getStartTime(), end).toMinutes();
        }).sum();
        return totalMinutes >= (long) maxWorkHours * 60;
    }

    @Transactional
    public DriverResponse update(Long id, UserUpdateRequest req) {
        Driver d = findById(id);
        if (req.getName() != null) d.setName(req.getName());
        if (req.getSurname() != null) d.setSurname(req.getSurname());
        if (req.getTelephoneNumber() != null) d.setTelephoneNumber(req.getTelephoneNumber());
        if (req.getAddress() != null) d.setAddress(req.getAddress());
        if (req.getProfilePicture() != null) d.setProfilePicture(req.getProfilePicture());
        return toResponse(driverRepo.save(d));
    }

    private Driver findById(Long id) {
        return driverRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
    }

    private DriverResponse toResponse(Driver d) {

        return DriverResponse.builder().id(d.getId()).email(d.getEmail()).name(d.getName()).surname(d.getSurname())
                .profilePicture(d.getProfilePicture()).telephoneNumber(d.getTelephoneNumber()).address(d.getAddress())
                .status(d.getStatus()).vehicleModel(d.getVehicleModel()).licensePlate(d.getLicensePlate())
                .vehicleType(d.getVehicleType()).passengerSeats(d.getPassengerSeats())
                .babyTransport(d.isBabyTransport()).petTransport(d.isPetTransport())
                .currentLatitude(d.getCurrentLatitude()).currentLongitude(d.getCurrentLongitude()).build();
    }
    
}
