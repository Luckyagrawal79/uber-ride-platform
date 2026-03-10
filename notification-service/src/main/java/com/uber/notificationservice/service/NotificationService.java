package com.uber.notificationservice.service;

import com.uber.common.event.NotificationEvent;
import com.uber.notificationservice.factory.*;
import com.uber.notificationservice.model.NotificationDocument;
import com.uber.notificationservice.repository.NotificationDocRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class NotificationService {

    private final NotificationDocRepository repo;
    private final NotificationSenderFactory senderFactory;

    public void processNotification(NotificationEvent event) {
        NotificationDocument doc = NotificationDocument.builder()
                .userId(event.getUserId()).userEmail(event.getUserEmail())
                .title(event.getTitle()).message(event.getMessage())
                .type(event.getType()).rideId(event.getRideId())
                .createdAt(LocalDateTime.now()).build();

        NotificationDocument saved = repo.save(doc);

        // Use Factory Pattern to get the right sender
        NotificationSender sender = senderFactory.getSender(event.getType());
        sender.send(saved);

        saved.setSent(true);
        repo.save(saved);
    }

    public List<NotificationDocument> getUserNotifications(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<NotificationDocument> getUnread(Long userId) {
        return repo.findByUserIdAndReadFalse(userId);
    }

    public void markAsRead(String notificationId) {
        repo.findById(notificationId).ifPresent(n -> { n.setRead(true); repo.save(n); });
    }

    public void markAllRead(Long userId) {
        repo.findByUserIdAndReadFalse(userId).forEach(n -> { n.setRead(true); repo.save(n); });
    }

    public long unreadCount(Long userId) { 
        return repo.countByUserIdAndReadFalse(userId); 
    }
}
