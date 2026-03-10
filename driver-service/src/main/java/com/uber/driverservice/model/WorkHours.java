package com.uber.driverservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "work_hours")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkHours {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false) 
    private Long driverId;

    @Column(nullable = false) 
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
}
