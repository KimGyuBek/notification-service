package com.threadly.notification.core.domain.notification;

import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 좋아요 도메인
 */
@Getter
@AllArgsConstructor
public class Notification {

  private String eventId;
  private String receiverId;
  private NotificationType notificationType;
  private LocalDateTime occurredAt;
  private ActorProfile actorProfile;
  private boolean isRead;
  private NotificationMetaData metadata;

  /**
   * 새로운 Notification 도메인 생성
   *
   * @param eventId
   * @param receiverId
   * @param notificationType
   * @param occurredAt
   * @param metadata
   * @return
   */
  public static Notification newNotification(String eventId, String receiverId,
      NotificationType notificationType,
      LocalDateTime occurredAt, ActorProfile actorProfile, NotificationMetaData metadata) {
    return new Notification(eventId, receiverId, notificationType, occurredAt, actorProfile, false,
        metadata);
  }

  /**
   * 알림 데이터 읽음 처리
   */
  public void markAsRead() {
    isRead = true;
  }

  /**
   * 행위자 프로필
   *
   * @param userId
   * @param nickname
   * @param profileImageUrl
   */
}
