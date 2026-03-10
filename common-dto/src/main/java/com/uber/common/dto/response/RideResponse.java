package com.uber.common.dto.response;
import com.uber.common.enums.*; import lombok.*;
import java.time.LocalDateTime; import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RideResponse {
    private Long id;
    private LocalDateTime startTime; private LocalDateTime endTime;
    private double totalCost; private double estimatedTimeMinutes;
    private RideStatus status; private VehicleType vehicleType;
    private boolean panicPressed; private boolean babyTransport; private boolean petTransport;
    private String rejectionReason; private LocalDateTime scheduledTime;
    private double departureLatitude; private double departureLongitude; private String departureAddress;
    private double destinationLatitude; private double destinationLongitude; private String destinationAddress;
    private double distanceKm;
    private Long driverId; private String driverName;
    private Long passengerId; private String passengerName;
    private PaymentMethod paymentMethod;
    private List<ReviewResponse> reviews;
}
