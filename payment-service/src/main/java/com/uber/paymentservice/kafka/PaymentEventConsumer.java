package com.uber.paymentservice.kafka;

import com.uber.common.event.PaymentRequestedEvent;
import com.uber.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class PaymentEventConsumer {
    
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment-requested", groupId = "payment-service-group")
    public void onPaymentRequested(PaymentRequestedEvent event) {
        log.info("Payment requested for ride {}", event.getRideId());
        paymentService.processPayment(event);
    }
}
