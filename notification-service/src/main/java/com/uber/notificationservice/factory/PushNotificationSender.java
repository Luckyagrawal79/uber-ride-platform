package com.uber.notificationservice.factory;

import com.uber.notificationservice.model.NotificationDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("pushSender") @Slf4j
public class PushNotificationSender implements NotificationSender {
    @Override
    public void send(NotificationDocument notification) {
        // Would integrate with FCM/APNs in production
        log.info("Push notification sent to user {}: {}", notification.getUserId(), notification.getTitle());
    }
}
