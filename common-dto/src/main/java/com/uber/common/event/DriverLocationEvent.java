package com.uber.common.event;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverLocationEvent implements Serializable {
    private Long driverId;
    private double latitude;
    private double longitude;
    private Long activeRideId;
    private LocalDateTime timestamp;
}
