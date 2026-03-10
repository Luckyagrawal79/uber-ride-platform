package com.uber.common.event;

import com.uber.common.enums.PaymentStatus;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentCompletedEvent implements Serializable {
    private Long paymentId;
    private Long rideId;
    private Long passengerId;
    private double amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime timestamp;
}
