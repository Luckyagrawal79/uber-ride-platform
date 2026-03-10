package com.uber.notificationservice.factory;

import com.uber.notificationservice.model.NotificationDocument;

/**
 * FACTORY PATTERN: Each notification channel (Push, Email, SMS, In-App)
 * has its own sender implementation.
 */
public interface NotificationSender {
    void send(NotificationDocument notification);
}
