package com.uber.common.dto.request;
import jakarta.validation.constraints.*; import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String name;
    @NotBlank private String surname;
    private String telephoneNumber;
    private String address;
    private String profilePicture;
}
