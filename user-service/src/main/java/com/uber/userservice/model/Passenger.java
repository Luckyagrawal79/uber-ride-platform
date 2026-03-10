package com.uber.userservice.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "passengers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Passenger {

    @Id 
    private Long id;  // Same ID as auth-service user

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false) 
    private String name;

    @Column(nullable = false) 
    private String surname;

    private String profilePicture;
    private String telephoneNumber;
    private String address;

    @Builder.Default
    private boolean blocked = false;

    @Builder.Default 
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
