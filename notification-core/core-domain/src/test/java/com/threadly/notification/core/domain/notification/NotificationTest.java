package com.threadly.notification.core.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Notification 도메인 테스트
 */
class NotificationTest {

  @Nested
  @DisplayName("Notification 생성 테스트")
  class NewNotificationTest {

    /*[Case #1] Notification.newNotification 호출 시 기본 값이 올바르게 설정되어야 한다*/
    @DisplayName("Notification 생성 성공 - 필드가 올바르게 초기화되어야 한다")
    @Test
    public void newNotification_shouldInitializeFieldsCorrectly() throws Exception {
      //given
      String eventId = "event-001";
      String receiverId = "receiver-123";
      NotificationType notificationType = NotificationType.POST_LIKE;
      LocalDateTime occurredAt = LocalDateTime.of(2024, 1, 1, 0, 0);
      ActorProfile actorProfile = new ActorProfile("actor-1", "nickname", "profile.png");
      PostLikeMeta metadata = new PostLikeMeta("post-123");

      //when
      Notification notification = Notification.newNotification(
          eventId,
          receiverId,
          notificationType,
          occurredAt,
          actorProfile,
          metadata
      );

      //then
      assertAll(
          () -> assertThat(notification.getEventId()).isEqualTo(eventId),
          () -> assertThat(notification.getReceiverId()).isEqualTo(receiverId),
          () -> assertThat(notification.getNotificationType()).isEqualTo(notificationType),
          () -> assertThat(notification.getOccurredAt()).isEqualTo(occurredAt),
          () -> assertThat(notification.getActorProfile()).isEqualTo(actorProfile),
          () -> assertThat(notification.getMetadata()).isEqualTo(metadata),
          () -> assertThat(notification.isRead()).isFalse()
      );
    }
  }

  @Nested
  @DisplayName("Notification 읽음 처리 테스트")
  class MarkAsReadTest {

    /*[Case #1] markAsRead 호출 시 읽음 상태가 true 로 변경되어야 한다*/
    @DisplayName("알림 읽음 처리 성공 - isRead 값이 true 로 변경되어야 한다")
    @Test
    public void markAsRead_shouldUpdateReadFlag() throws Exception {
      //given
      Notification notification = Notification.newNotification(
          "event-002",
          "receiver-456",
          NotificationType.COMMENT_ADDED,
          LocalDateTime.of(2024, 1, 2, 12, 30),
          new ActorProfile("actor-2", "commenter", "profile2.png"),
          new PostCommentMeta("post-456", "comment-1", "comment")
      );

      //when
      notification.markAsRead();

      //then
      assertThat(notification.isRead()).isTrue();
    }
  }
}
