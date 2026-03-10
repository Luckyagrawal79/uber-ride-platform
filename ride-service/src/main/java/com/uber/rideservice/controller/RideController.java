package com.uber.rideservice.controller;

import com.uber.common.dto.request.*;
import com.uber.common.dto.response.*;
import com.uber.common.enums.VehicleType;
import com.uber.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/rides") @RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    // Ride estimate (public, no auth needed)
    @PostMapping("/estimate")
    public ResponseEntity<RideEstimateResponse> estimate(
            @RequestParam double depLat, @RequestParam double depLng,
            @RequestParam double destLat, @RequestParam double destLng,
            @RequestParam(defaultValue = "STANDARD") VehicleType type) {
        return ResponseEntity.ok(rideService.estimate(
                new LocationDto(depLat, depLng, null), new LocationDto(destLat, destLng, null), type));
    }

    @PostMapping
    public ResponseEntity<RideResponse> create(
            @RequestHeader("X-User-Id") Long userId, @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody RideRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createRide(userId, email, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<RideResponse> accept(@PathVariable Long id, @RequestHeader("X-User-Id") Long driverId) {
        return ResponseEntity.ok(rideService.acceptRide(id, driverId));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RideResponse> reject(@PathVariable Long id, @RequestHeader("X-User-Id") Long driverId,
                                                @Valid @RequestBody RejectRequest req) {
        return ResponseEntity.ok(rideService.rejectRide(id, driverId, req.getReason()));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<RideResponse> start(@PathVariable Long id) { 
        return ResponseEntity.ok(rideService.startRide(id)); 
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<RideResponse> finish(@PathVariable Long id) { 
        return ResponseEntity.ok(rideService.finishRide(id)); 
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RideResponse> cancel(@PathVariable Long id) { 
        return ResponseEntity.ok(rideService.cancelRide(id));
     }

    @PutMapping("/{id}/panic")
    public ResponseEntity<Void> panic(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody PanicRequest req) {
        rideService.triggerPanic(id, userId, req.getReason());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<PaginatedResponse<RideResponse>> passengerRides(@PathVariable Long passengerId, Pageable pageable) {
        return ResponseEntity.ok(rideService.getPassengerRides(passengerId, pageable));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<PaginatedResponse<RideResponse>> driverRides(@PathVariable Long driverId, Pageable pageable) {
        return ResponseEntity.ok(rideService.getDriverRides(driverId, pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<RideResponse> activeRide(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "false") boolean isDriver) {
        return ResponseEntity.ok(rideService.getActiveRide(userId, isDriver));
    }

    // Reviews
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponse> review(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId,
                                                   @RequestHeader("X-User-Email") String email, @Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createReview(id, userId, email, req));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewResponse>> rideReviews(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideReviews(id));
    }

    @GetMapping("/driver/{driverId}/reviews")
    public ResponseEntity<List<ReviewResponse>> driverReviews(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideService.getDriverReviews(driverId));
    }

    // Reports
    @GetMapping("/report")
    public ResponseEntity<ReportResponse> report(@RequestHeader("X-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "false") boolean isDriver,
                                                   @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ResponseEntity.ok(rideService.generateReport(userId, isDriver, from, to));
    }
}
