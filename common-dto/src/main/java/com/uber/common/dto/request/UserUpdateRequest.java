package com.uber.common.dto.request;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserUpdateRequest {
    private String name; 
    private String surname; 
    private String profilePicture;
    private String telephoneNumber; 
    private String address; 
    private String email;
     private String newPassword;
}
