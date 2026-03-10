package com.uber.userservice.model;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "favorite_routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FavoriteRoute {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false) 
    private String name;

    @Column(nullable = false) 
    private Long passengerId;

    private double departureLatitude; 
    private double departureLongitude;
    private String departureAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private String destinationAddress;
}
