package com.uber.notificationservice.repository;
import com.uber.notificationservice.model.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationDocRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationDocument> findByUserIdAndReadFalse(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}
