package com.uber.common.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private final String tokenType = "Bearer";
    private UserResponse user;
}
