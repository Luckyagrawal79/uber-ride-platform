package com.uber.common.dto.request;
import com.uber.common.enums.VehicleType;
import jakarta.validation.constraints.*; 
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateDriverRequest {
    
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String name;
    @NotBlank private String surname;
    private String telephoneNumber;
    private String address;
    @NotBlank private String vehicleModel;
    @NotBlank private String licensePlate;
    @NotNull private VehicleType vehicleType;
    private int passengerSeats = 4;
    private boolean babyTransport;
    private boolean petTransport;
}
