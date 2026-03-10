package com.uber.notificationservice.factory;

import com.uber.notificationservice.model.NotificationDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("emailSender") @Slf4j
public class EmailNotificationSender implements NotificationSender {
    // In production, inject JavaMailSender here
    @Override
    public void send(NotificationDocument notification) {
        // Would use JavaMailSender to send email
        log.info("Email notification sent to {}: {}", notification.getUserEmail(), notification.getTitle());
    }
}
