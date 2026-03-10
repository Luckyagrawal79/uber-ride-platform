package com.uber.notificationservice.factory;

import com.uber.notificationservice.model.NotificationDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("smsSender") @Slf4j
public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(NotificationDocument notification) {
        // Would integrate with Twilio/MSG91 in production
        log.info("SMS notification sent to user {}: {}", notification.getUserId(), notification.getTitle());
    }
}
