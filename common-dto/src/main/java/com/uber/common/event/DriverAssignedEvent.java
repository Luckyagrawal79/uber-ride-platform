package com.uber.common.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverAssignedEvent implements Serializable {
    private Long rideId;
    private Long driverId;
    private Long passengerId;
    private String driverName;
    private String vehicleModel;
    private String licensePlate;
    private double driverLatitude;
    private double driverLongitude;
    private LocalDateTime timestamp;
}
