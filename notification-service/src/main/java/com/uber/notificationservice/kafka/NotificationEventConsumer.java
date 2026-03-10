package com.uber.notificationservice.kafka;

import com.uber.common.event.*;
import com.uber.common.enums.NotificationType;
import com.uber.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component @RequiredArgsConstructor @Slf4j
public class NotificationEventConsumer {
    private final NotificationService service;

    @KafkaListener(topics = "notification-requested", groupId = "notification-service-group")
    public void onNotificationRequested(NotificationEvent event) {
        log.info("Notification requested for user {}: {}", event.getUserId(), event.getTitle());
        service.processNotification(event);
    }

    @KafkaListener(topics = "ride-status-changed", groupId = "notification-service-group")
    public void onRideStatusChanged(RideStatusChangedEvent event) {
        if (event.getPassengerId() != null) {
            service.processNotification(NotificationEvent.builder()
                    .userId(event.getPassengerId())
                    .title("Ride Update")
                    .message("Your ride status changed to: " + event.getNewStatus())
                    .type(NotificationType.IN_APP).rideId(event.getRideId())
                    .timestamp(LocalDateTime.now()).build());
        }
    }

    @KafkaListener(topics = "payment-completed", groupId = "notification-service-group")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        service.processNotification(NotificationEvent.builder()
                .userId(event.getPassengerId())
                .title("Payment Receipt")
                .message("Payment of ₹" + event.getAmount() + " processed. Transaction: " + event.getTransactionId())
                .type(NotificationType.IN_APP).rideId(event.getRideId())
                .timestamp(LocalDateTime.now()).build());
    }
}
