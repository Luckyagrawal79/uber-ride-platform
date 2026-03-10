package com.uber.common.event;

import com.uber.common.enums.RideStatus;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RideStatusChangedEvent implements Serializable {
    private Long rideId;
    private Long driverId;
    private Long passengerId;
    private RideStatus previousStatus;
    private RideStatus newStatus;
    private String reason;
    private double totalCost;
    private LocalDateTime timestamp;
}
