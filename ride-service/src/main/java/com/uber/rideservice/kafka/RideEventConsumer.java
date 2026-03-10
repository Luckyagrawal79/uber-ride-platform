package com.uber.rideservice.kafka;

import com.uber.common.event.*;
import com.uber.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class RideEventConsumer {
    private final RideService rideService;

    @KafkaListener(topics = "driver-assigned", groupId = "ride-service-group")
    public void onDriverAssigned(DriverAssignedEvent event) {
        log.info("Driver {} assigned to ride {}", event.getDriverId(), event.getRideId());
        rideService.onDriverAssigned(event);
    }

    @KafkaListener(topics = "payment-completed", groupId = "ride-service-group")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment {} completed for ride {}", event.getPaymentId(), event.getRideId());
        // Could update ride with payment confirmation
    }
}
