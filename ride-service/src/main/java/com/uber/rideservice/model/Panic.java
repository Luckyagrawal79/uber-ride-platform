package com.uber.rideservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "panics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Panic {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private Long rideId;
    private Long userId;

    @Column(length = 500) 
    private String reason;
    
    private boolean resolved;
    private LocalDateTime timestamp;
}
