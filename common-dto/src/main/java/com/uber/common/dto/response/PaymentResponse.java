package com.uber.common.dto.response;
import com.uber.common.enums.*; import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private Long id; 
    private Long rideId; 
    private Long passengerId;
    private double amount; 
    private PaymentMethod method; 
    private PaymentStatus status;
    private String transactionId; 
    private LocalDateTime processedAt;
}
