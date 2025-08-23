package com.threadly.notification.adapter.persistence.adapter;

import com.threadly.notification.adapter.persistence.entity.NotificationEntity;
import com.threadly.notification.adapter.persistence.repository.MongoNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceAdapter {

  private final MongoNotificationRepository mongoNotificationRepository;

  public void saveLikeNotification(String userId, String postId) {
    log.info("Saving like notification - userId: {}, postId: {}", userId, postId);
    
    NotificationEntity notification = NotificationEntity.builder()
        .userId(userId)
        .postId(postId)
        .notificationType("LIKE")
        .content(String.format("Your post %s received a like!", postId))
        .isRead(false)
        .build();

    NotificationEntity saved = mongoNotificationRepository.save(notification);
    log.info("Saved notification with id: {}", saved.getId());
  }

  public void removeLikeNotification(String userId, String postId) {
    log.info("Removing like notification - userId: {}, postId: {}", userId, postId);
    
    // 좋아요 알림 삭제 로직 (실제로는 soft delete 또는 상태 변경)
    mongoNotificationRepository.findLikeNotificationsByPostId(postId)
        .stream()
        .filter(notification -> notification.getUserId().equals(userId))
        .forEach(mongoNotificationRepository::delete);
  }

  public long getUnreadNotificationCount(String userId) {
    return mongoNotificationRepository.countByUserIdAndIsReadFalse(userId);
  }
}