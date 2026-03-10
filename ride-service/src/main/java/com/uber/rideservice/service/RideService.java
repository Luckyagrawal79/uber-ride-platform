package com.uber.rideservice.service;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.common.enums.*;
import com.uber.common.event.*;
import com.uber.rideservice.model.*;
import com.uber.rideservice.repository.*;
import com.uber.rideservice.state.RideState;
import com.uber.rideservice.state.RideStateFactory;
import com.uber.rideservice.strategy.PricingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service @Slf4j
public class RideService {

    private final RideRepository rideRepo;
    private final ReviewRepository reviewRepo;
    private final PanicRepository panicRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PricingStrategy pricingStrategy;

    private static final double AVG_SPEED_KM_PER_MIN = 0.5;
    private static final List<RideStatus> ACTIVE_STATUSES = List.of(RideStatus.PENDING, RideStatus.ACCEPTED, RideStatus.STARTED);

    public RideService(RideRepository rideRepo, ReviewRepository reviewRepo, PanicRepository panicRepo,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        @Qualifier("standardPricing") PricingStrategy pricingStrategy) {
        this.rideRepo = rideRepo;
        this.reviewRepo = reviewRepo;
        this.panicRepo = panicRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.pricingStrategy = pricingStrategy;
    }

    // ===== RIDE LIFECYCLE (uses State Pattern) =====

    @Transactional
    public RideResponse createRide(Long passengerId, String passengerName, RideRequest req) {
        // Check for existing active ride
        if (!rideRepo.findByPassengerIdAndStatusIn(passengerId, ACTIVE_STATUSES).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have an active ride");
        }

        double distance = haversine(req.getDeparture().getLatitude(), req.getDeparture().getLongitude(),
                req.getDestination().getLatitude(), req.getDestination().getLongitude());
        double cost = pricingStrategy.calculatePrice(distance, req.getVehicleType());
        double estimatedTime = distance / AVG_SPEED_KM_PER_MIN;

        boolean isScheduled = req.getScheduledTime() != null;
        Ride ride = Ride.builder()
                .passengerId(passengerId).passengerName(passengerName)
                .status(isScheduled ? RideStatus.SCHEDULED : RideStatus.PENDING)
                .vehicleType(req.getVehicleType())
                .paymentMethod(req.getPaymentMethod())
                .departureLatitude(req.getDeparture().getLatitude())
                .departureLongitude(req.getDeparture().getLongitude())
                .departureAddress(req.getDeparture().getAddress())
                .destinationLatitude(req.getDestination().getLatitude())
                .destinationLongitude(req.getDestination().getLongitude())
                .destinationAddress(req.getDestination().getAddress())
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .totalCost(cost).estimatedTimeMinutes(Math.round(estimatedTime * 10.0) / 10.0)
                .babyTransport(req.isBabyTransport())
                .petTransport(req.isPetTransport())
                .scheduledTime(req.getScheduledTime())
                .createdAt(LocalDateTime.now()).build();

        Ride saved = rideRepo.save(ride);

        // Publish event to find a driver (if not scheduled)
        if (!isScheduled) {
            publishRideRequestedEvent(saved);
        }

        return toResponse(saved);
    }


    @Transactional
    public RideResponse acceptRide(Long rideId, Long driverId) {
        Ride ride = findById(rideId);
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.accept(ride);
        Ride saved = rideRepo.save(ride);
        publishStatusChange(saved, RideStatus.PENDING);
        return toResponse(saved);
    }


    @Transactional
    public RideResponse rejectRide(Long rideId, Long driverId, String reason) {
        Ride ride = findById(rideId);
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.reject(ride, reason);
        Ride saved = rideRepo.save(ride);
        publishStatusChange(saved, RideStatus.PENDING);
        return toResponse(saved);
    }


    @Transactional
    public RideResponse startRide(Long rideId) {
        Ride ride = findById(rideId);
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.start(ride);
        Ride saved = rideRepo.save(ride);
        publishStatusChange(saved, RideStatus.ACCEPTED);
        return toResponse(saved);
    }


    @Transactional
    public RideResponse finishRide(Long rideId) {
        Ride ride = findById(rideId);
        RideStatus prevStatus = ride.getStatus();
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.finish(ride);
        Ride saved = rideRepo.save(ride);
        publishStatusChange(saved, prevStatus);

        // SAGA: Trigger payment after ride finishes
        kafkaTemplate.send("payment-requested", String.valueOf(rideId),
                PaymentRequestedEvent.builder().rideId(rideId).passengerId(ride.getPassengerId())
                        .driverId(ride.getDriverId()).amount(ride.getTotalCost())
                        .paymentMethod(ride.getPaymentMethod()).timestamp(LocalDateTime.now()).build());

        return toResponse(saved);
    }


    @Transactional
    public RideResponse cancelRide(Long rideId) {
        Ride ride = findById(rideId);
        RideStatus prevStatus = ride.getStatus();
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.cancel(ride);
        Ride saved = rideRepo.save(ride);
        publishStatusChange(saved, prevStatus);
        return toResponse(saved);
    }


    @Transactional
    public void triggerPanic(Long rideId, Long userId, String reason) {
        Ride ride = findById(rideId);
        RideState state = RideStateFactory.getState(ride.getStatus());
        state.panic(ride);
        rideRepo.save(ride);

        panicRepo.save(Panic.builder().rideId(rideId).userId(userId).reason(reason)
                .resolved(false).timestamp(LocalDateTime.now()).build());

        // Notify admins via Kafka
        kafkaTemplate.send("notification-requested", NotificationEvent.builder()
                .title("PANIC ALERT").message("Panic on ride #" + rideId + ": " + reason)
                .type(NotificationType.PUSH).rideId(rideId).timestamp(LocalDateTime.now()).build());
    }


    // Called by Kafka when driver is assigned
    @Transactional
    public void onDriverAssigned(DriverAssignedEvent event) {
        rideRepo.findById(event.getRideId()).ifPresent(ride -> {
            ride.setDriverId(event.getDriverId());
            ride.setDriverName(event.getDriverName());
            rideRepo.save(ride);
            log.info("Driver {} assigned to ride {}", event.getDriverId(), event.getRideId());

            // Notify passenger
            kafkaTemplate.send("notification-requested", NotificationEvent.builder()
                    .userId(ride.getPassengerId()).title("Driver Assigned")
                    .message("Driver " + event.getDriverName() + " is on the way! Vehicle: " + event.getVehicleModel())
                    .type(NotificationType.IN_APP).rideId(ride.getId()).timestamp(LocalDateTime.now()).build());
        });
    }

    // ===== QUERIES =====

    public RideResponse getRideById(Long id) { 
        return toResponse(findById(id)); 
    }


    public PaginatedResponse<RideResponse> getPassengerRides(Long passengerId, Pageable pageable) {
        Page<Ride> page = rideRepo.findByPassengerId(passengerId, pageable);
        return new PaginatedResponse<>((int) page.getTotalElements(), page.map(this::toResponse).toList());
    }


    public PaginatedResponse<RideResponse> getDriverRides(Long driverId, Pageable pageable) {
        Page<Ride> page = rideRepo.findByDriverId(driverId, pageable);
        return new PaginatedResponse<>((int) page.getTotalElements(), page.map(this::toResponse).toList());
    }


    public RideResponse getActiveRide(Long userId, boolean isDriver) {
        List<Ride> active = isDriver ?
                rideRepo.findByDriverIdAndStatusIn(userId, ACTIVE_STATUSES) :
                rideRepo.findByPassengerIdAndStatusIn(userId, ACTIVE_STATUSES);
        if (active.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active ride");
        return toResponse(active.getFirst());
    }


    public RideEstimateResponse estimate(LocationDto departure, LocationDto destination, VehicleType vehicleType) {
        double dist = haversine(departure.getLatitude(), departure.getLongitude(), destination.getLatitude(), destination.getLongitude());
        double cost = pricingStrategy.calculatePrice(dist, vehicleType);
        return RideEstimateResponse.builder().distanceKm(Math.round(dist * 100.0) / 100.0)
                .estimatedCost(cost).estimatedTimeMinutes(Math.round(dist / AVG_SPEED_KM_PER_MIN * 10.0) / 10.0).build();
    }


    // ===== REVIEWS =====

    
    @Transactional
    public ReviewResponse createReview(Long rideId, Long passengerId, String passengerName, ReviewRequest req) {
        Ride ride = findById(rideId);

        if (ride.getStatus() != RideStatus.FINISHED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only review finished rides");

        Review review = Review.builder().rideId(rideId).passengerId(passengerId).passengerName(passengerName)
                .driverId(ride.getDriverId()).driverRating(req.getDriverRating()).driverComment(req.getDriverComment())
                .vehicleRating(req.getVehicleRating()).vehicleComment(req.getVehicleComment())
                .createdAt(LocalDateTime.now()).build();

        return toReviewResponse(reviewRepo.save(review));
    }


    public List<ReviewResponse> getRideReviews(Long rideId) {
        return reviewRepo.findByRideId(rideId).stream().map(this::toReviewResponse).toList();
    }


    public List<ReviewResponse> getDriverReviews(Long driverId) {
        return reviewRepo.findByDriverId(driverId).stream().map(this::toReviewResponse).toList();
    }


    // ===== REPORTS =====


    public ReportResponse generateReport(Long userId, boolean isDriver, LocalDate from, LocalDate to) {
        List<Ride> rides = isDriver ?
                rideRepo.findFinishedByDriverAndDateRange(userId, from.atStartOfDay(), to.atTime(LocalTime.MAX)) :
                rideRepo.findFinishedByPassengerAndDateRange(userId, from.atStartOfDay(), to.atTime(LocalTime.MAX));

        Double avgRating = isDriver ? reviewRepo.findAverageDriverRating(userId) : null;

        Map<LocalDate, Integer> ridesPerDay = rides.stream().filter(r -> r.getStartTime() != null)
                .collect(Collectors.groupingBy(r -> r.getStartTime().toLocalDate(), TreeMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)));

        Map<LocalDate, Double> earningsPerDay = rides.stream().filter(r -> r.getStartTime() != null)
                .collect(Collectors.groupingBy(r -> r.getStartTime().toLocalDate(), TreeMap::new,
                        Collectors.summingDouble(Ride::getTotalCost)));

        return ReportResponse.builder().totalRides(rides.size())
                .totalDistance(rides.stream().mapToDouble(Ride::getDistanceKm).sum())
                .totalEarnings(rides.stream().mapToDouble(Ride::getTotalCost).sum())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .ridesPerDay(ridesPerDay).earningsPerDay(earningsPerDay).build();
    }


    // ===== SCHEDULED RIDE PROCESSING =====

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processScheduledRides() {
        List<Ride> scheduled = rideRepo.findByStatusAndScheduledTimeBefore(RideStatus.SCHEDULED, LocalDateTime.now());
        for (Ride ride : scheduled) {
            ride.setStatus(RideStatus.PENDING);
            rideRepo.save(ride);
            publishRideRequestedEvent(ride);
            log.info("Scheduled ride {} now pending", ride.getId());
        }
    }


    // ===== PRIVATE HELPERS =====

    private Ride findById(Long id) {
        return rideRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
    }


    private void publishRideRequestedEvent(Ride ride) {
        kafkaTemplate.send("ride-requested", String.valueOf(ride.getId()),
                RideRequestedEvent.builder().rideId(ride.getId()).passengerId(ride.getPassengerId())
                        .vehicleType(ride.getVehicleType())
                        .babyTransport(ride.isBabyTransport())
                        .petTransport(ride.isPetTransport())
                        .departureLatitude(ride.getDepartureLatitude())
                        .departureLongitude(ride.getDepartureLongitude())
                        .departureAddress(ride.getDepartureAddress())
                        .destinationLatitude(ride.getDestinationLatitude())
                        .destinationLongitude(ride.getDestinationLongitude())
                        .destinationAddress(ride.getDestinationAddress())
                        .estimatedDistance(ride.getDistanceKm())
                        .estimatedCost(ride.getTotalCost())
                        .timestamp(LocalDateTime.now()).build());
    }


    private void publishStatusChange(Ride ride, RideStatus previousStatus) {
        kafkaTemplate.send("ride-status-changed", String.valueOf(ride.getId()),
                RideStatusChangedEvent.builder().rideId(ride.getId()).driverId(ride.getDriverId())
                        .passengerId(ride.getPassengerId()).previousStatus(previousStatus)
                        .newStatus(ride.getStatus()).totalCost(ride.getTotalCost())
                        .timestamp(LocalDateTime.now()).build());
    }


    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


    private RideResponse toResponse(Ride r) {
        return RideResponse.builder().id(r.getId()).startTime(r.getStartTime()).endTime(r.getEndTime())
                .totalCost(r.getTotalCost()).estimatedTimeMinutes(r.getEstimatedTimeMinutes())
                .status(r.getStatus()).vehicleType(r.getVehicleType()).panicPressed(r.isPanicPressed())
                .babyTransport(r.isBabyTransport()).petTransport(r.isPetTransport())
                .rejectionReason(r.getRejectionReason()).scheduledTime(r.getScheduledTime())
                .departureLatitude(r.getDepartureLatitude()).departureLongitude(r.getDepartureLongitude())
                .departureAddress(r.getDepartureAddress()).destinationLatitude(r.getDestinationLatitude())
                .destinationLongitude(r.getDestinationLongitude()).destinationAddress(r.getDestinationAddress())
                .distanceKm(r.getDistanceKm()).driverId(r.getDriverId()).driverName(r.getDriverName())
                .passengerId(r.getPassengerId()).passengerName(r.getPassengerName())
                .paymentMethod(r.getPaymentMethod()).build();
    }

    
    private ReviewResponse toReviewResponse(Review r) {
        return ReviewResponse.builder().id(r.getId()).driverRating(r.getDriverRating())
                .driverComment(r.getDriverComment()).vehicleRating(r.getVehicleRating())
                .vehicleComment(r.getVehicleComment()).createdAt(r.getCreatedAt())
                .passengerId(r.getPassengerId()).passengerName(r.getPassengerName()).build();
    }
    
}
