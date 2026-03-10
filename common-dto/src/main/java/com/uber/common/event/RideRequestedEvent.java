package com.uber.common.event;

import com.uber.common.enums.VehicleType;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RideRequestedEvent implements Serializable {
    private Long rideId;
    private Long passengerId;
    private VehicleType vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private double departureLatitude;
    private double departureLongitude;
    private String departureAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private String destinationAddress;
    private double estimatedDistance;
    private double estimatedCost;
    private LocalDateTime timestamp;
}
