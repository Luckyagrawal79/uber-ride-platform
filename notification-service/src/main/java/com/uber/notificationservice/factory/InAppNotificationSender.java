package com.uber.notificationservice.factory;

import com.uber.notificationservice.model.NotificationDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component("inAppSender") @RequiredArgsConstructor @Slf4j
public class InAppNotificationSender implements NotificationSender {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void send(NotificationDocument notification) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUserId()),
                "/queue/notifications",
                notification);
        log.info("In-app notification sent to user {}", notification.getUserId());
    }
}
