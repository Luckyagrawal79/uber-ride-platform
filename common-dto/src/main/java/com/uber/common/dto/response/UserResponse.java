package com.uber.common.dto.response;
import com.uber.common.enums.Role; import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {

    private Long id; 
    private String email;
    private String name; 
    private String surname;
    private String profilePicture;
    private String telephoneNumber; 
    private String address;
    private Role role; 
    private boolean blocked;
    private boolean active;
}
