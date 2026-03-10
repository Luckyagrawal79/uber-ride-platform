package com.uber.paymentservice.strategy;
import lombok.*;

@Data @Builder @AllArgsConstructor
public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String failureReason;
}
