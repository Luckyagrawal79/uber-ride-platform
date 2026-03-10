package com.uber.notificationservice.controller;

import com.uber.notificationservice.model.NotificationDocument;
import com.uber.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDocument>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserNotifications(userId));
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationDocument>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnread(userId));
    }

    @GetMapping("/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> unreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("count", service.unreadCount(userId)));
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<Void> markRead(@PathVariable String notificationId) {
        service.markAsRead(notificationId); 
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
        service.markAllRead(userId); 
        return ResponseEntity.noContent().build();
    }
}
