package com.uber.common.dto.response;
import com.uber.common.enums.*; import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverResponse {
    private Long id; private String email; private String name; private String surname;
    private String profilePicture; private String telephoneNumber; private String address;
    private DriverStatus status;
    private String vehicleModel; private String licensePlate; private VehicleType vehicleType;
    private int passengerSeats; private boolean babyTransport; private boolean petTransport;
    private Double currentLatitude; private Double currentLongitude;
    private Double averageRating;
}
