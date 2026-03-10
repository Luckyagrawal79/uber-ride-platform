package com.uber.notificationservice.model;

import com.uber.common.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDocument {

    @Id private String id;
    @Indexed private Long userId;

    private String userEmail;
    private String title;
    private String message;
    private NotificationType type;
    private Long rideId;

    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private boolean sent = false;
    
    private LocalDateTime createdAt;
}
