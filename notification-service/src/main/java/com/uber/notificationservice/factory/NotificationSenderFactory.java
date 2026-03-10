package com.uber.notificationservice.factory;

import com.uber.common.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Factory that returns the correct NotificationSender based on type.
 */
@Component @RequiredArgsConstructor
public class NotificationSenderFactory {
    private final Map<String, NotificationSender> senders;

    public NotificationSender getSender(NotificationType type) {
        String key = switch (type) {
            case IN_APP -> "inAppSender";
            case EMAIL -> "emailSender";
            case PUSH -> "pushSender";
            case SMS -> "smsSender";
        };
        return senders.get(key);
    }
}
