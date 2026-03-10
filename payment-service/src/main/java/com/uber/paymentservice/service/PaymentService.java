package com.uber.paymentservice.service;

import com.uber.common.dto.response.PaymentResponse;
import com.uber.common.enums.*;
import com.uber.common.event.*;
import com.uber.paymentservice.model.Payment;
import com.uber.paymentservice.repository.PaymentRepository;
import com.uber.paymentservice.strategy.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor @Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, PaymentProcessor> processors;

    @Transactional
    public PaymentResponse processPayment(PaymentRequestedEvent event) {
        log.info("Processing payment for ride {}: {} via {}", event.getRideId(), event.getAmount(), event.getPaymentMethod());

        // Select strategy based on payment method
        String processorKey = switch (event.getPaymentMethod()) {
            case CASH -> "cashProcessor";
            case CREDIT_CARD -> "creditCardProcessor";
            case WALLET -> "walletProcessor";
        };

        PaymentProcessor processor = processors.get(processorKey);
        PaymentResult result = processor.process(event.getRideId(), event.getPassengerId(), event.getAmount());

        Payment payment = Payment.builder()
                .rideId(event.getRideId()).passengerId(event.getPassengerId()).driverId(event.getDriverId())
                .amount(event.getAmount()).method(event.getPaymentMethod())
                .status(result.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .transactionId(result.getTransactionId()).failureReason(result.getFailureReason())
                .createdAt(LocalDateTime.now()).processedAt(LocalDateTime.now()).build();

        Payment saved = paymentRepo.save(payment);

        // Publish payment completion event (part of the Saga)
        kafkaTemplate.send("payment-completed", String.valueOf(event.getRideId()),
                PaymentCompletedEvent.builder().paymentId(saved.getId()).rideId(saved.getRideId())
                        .passengerId(saved.getPassengerId()).amount(saved.getAmount())
                        .status(saved.getStatus()).transactionId(saved.getTransactionId())
                        .timestamp(LocalDateTime.now()).build());

        // Also send notification
        kafkaTemplate.send("notification-requested", NotificationEvent.builder()
                .userId(event.getPassengerId()).title("Payment Processed")
                .message("Payment of ₹" + event.getAmount() + " " + (result.isSuccess() ? "successful" : "failed"))
                .type(NotificationType.IN_APP).rideId(event.getRideId()).timestamp(LocalDateTime.now()).build());

        return toResponse(saved);
    }

    public List<PaymentResponse> getByPassenger(Long passengerId) {
        return paymentRepo.findByPassengerId(passengerId).stream().map(this::toResponse).toList();
    }

    public PaymentResponse getByRide(Long rideId) {
        return paymentRepo.findByRideId(rideId).map(this::toResponse).orElse(null);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder().id(p.getId()).rideId(p.getRideId()).passengerId(p.getPassengerId())
                .amount(p.getAmount()).method(p.getMethod()).status(p.getStatus())
                .transactionId(p.getTransactionId()).processedAt(p.getProcessedAt()).build();
    }
}
