package com.uber.driverservice.model;

import com.uber.common.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "drivers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Driver {

    @Id private Long id;

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false) 
    private String name;

    @Column(nullable = false) 
    private String surname;

    private String profilePicture;
    private String telephoneNumber;
    private String address;

    @Enumerated(EnumType.STRING) 
    @Builder.Default
    private DriverStatus status = DriverStatus.UNAVAILABLE;

    private String vehicleModel;
    private String licensePlate;

    @Enumerated(EnumType.STRING) 
    private VehicleType vehicleType;

    @Builder.Default
    private int passengerSeats = 4;


    private boolean babyTransport;
    private boolean petTransport;
    private Double currentLatitude;
    private Double currentLongitude;

    @Builder.Default
    private boolean blocked = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
