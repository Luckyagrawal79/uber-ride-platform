package com.uber.common.event;

import com.uber.common.enums.NotificationType;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationEvent implements Serializable {
    private Long userId;
    private String userEmail;
    private String title;
    private String message;
    private NotificationType type;
    private Long rideId;
    private LocalDateTime timestamp;
}
