package com.uber.rideservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    private Long rideId;
    private Long passengerId;
    private String passengerName;
    private Long driverId;
    private int driverRating;
    private String driverComment;
    private int vehicleRating;
    private String vehicleComment;
    private LocalDateTime createdAt;
}
