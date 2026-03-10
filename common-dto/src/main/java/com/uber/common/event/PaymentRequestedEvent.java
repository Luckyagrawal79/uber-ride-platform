package com.uber.common.event;

import com.uber.common.enums.PaymentMethod;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentRequestedEvent implements Serializable {
    private Long rideId;
    private Long passengerId;
    private Long driverId;
    private double amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime timestamp;
}
