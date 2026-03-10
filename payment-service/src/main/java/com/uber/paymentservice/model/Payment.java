package com.uber.paymentservice.model;
import com.uber.common.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false) 
    private Long rideId;

    @Column(nullable = false) 
    private Long passengerId;

    private Long driverId;
    private double amount;

    @Enumerated(EnumType.STRING) 
    private PaymentMethod method;

    @Enumerated(EnumType.STRING) 
    private PaymentStatus status;
    
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
