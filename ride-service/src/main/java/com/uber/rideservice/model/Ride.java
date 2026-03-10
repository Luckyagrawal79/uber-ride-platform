package com.uber.rideservice.model;

import com.uber.common.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "rides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ride {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private Long passengerId;
    private String passengerName;
    private Long driverId;
    private String driverName;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false) 
    private RideStatus status;

    @Enumerated(EnumType.STRING) 
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING) 
    private PaymentMethod paymentMethod;

    private double departureLatitude; 
    private double departureLongitude;
    private String departureAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private String destinationAddress;
    private double distanceKm;
    private double totalCost;
    private double estimatedTimeMinutes;
    private boolean panicPressed;
    private boolean babyTransport;
    private boolean petTransport;
    private String rejectionReason;
    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
