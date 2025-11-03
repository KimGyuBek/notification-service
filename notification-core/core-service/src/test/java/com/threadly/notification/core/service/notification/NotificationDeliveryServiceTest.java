package com.threadly.notification.core.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowAcceptMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.out.NotificationPushPort;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage.Payload;
import com.threadly.notification.core.port.notification.out.dto.preview.CommentLikePreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowAcceptPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowRequestPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.PostCommentPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.PostLikePreview;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import java.time.LocalDateTime;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationDeliveryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class NotificationDeliveryServiceTest {

  @InjectMocks
  private NotificationDeliveryService notificationDeliveryService;

  @Mock
  private NotificationPushPort notificationPushPort;

  private Notification baseNotification(NotificationType type,
      com.threadly.notification.core.domain.notification.metadata.NotificationMetaData metadata) {
    return Notification.newNotification(
        "event-1",
        "receiver-1",
        type,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        metadata
    );
  }

  private NotificationMessage capturedMessage(NotificationPushCommand command) {
    notificationDeliveryService.pushNotification(command);
    ArgumentCaptor<NotificationMessage> captor = ArgumentCaptor.forClass(NotificationMessage.class);
    verify(notificationPushPort).pushToUser(eq("receiver-1"), captor.capture());
    return captor.getValue();
  }

  @Order(1)
  @Nested
  @DisplayName("NotificationType 별 Preview 생성 테스트")
  class PreviewGenerationTest {

    /*[Case #1] POST_LIKE 타입이면 PostLikePreview가 생성되어야 한다*/
    @Order(1)
    @DisplayName("1. POST_LIKE 타입일 때 PostLikePreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreatePostLikePreview_whenTypeIsPostLike() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.POST_LIKE,
          new PostLikeMeta("post-1"));
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.POST_LIKE, PostLikePreview.class);
    }

    /*[Case #2] COMMENT_ADDED 타입이면 PostCommentPreview가 생성되어야 한다*/
    @Order(2)
    @DisplayName("2. COMMENT_ADDED 타입일 때 PostCommentPreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreatePostCommentPreview_whenTypeIsCommentAdded() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.COMMENT_ADDED,
          new PostCommentMeta("post-1", "comment-1", "내용"));
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.COMMENT_ADDED, PostCommentPreview.class);
    }

    /*[Case #3] COMMENT_LIKE 타입이면 CommentLikePreview가 생성되어야 한다*/
    @Order(3)
    @DisplayName("3. COMMENT_LIKE 타입일 때 CommentLikePreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreateCommentLikePreview_whenTypeIsCommentLike() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.COMMENT_LIKE,
          new CommentLikeMeta("post-1", "comment-1", "내용"));
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.COMMENT_LIKE, CommentLikePreview.class);
    }

    /*[Case #4] FOLLOW 타입이면 FollowPreview가 생성되어야 한다*/
    @Order(4)
    @DisplayName("4. FOLLOW 타입일 때 FollowPreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreateFollowPreview_whenTypeIsFollow() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.FOLLOW, new FollowMeta());
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.FOLLOW, FollowPreview.class);
    }

    /*[Case #5] FOLLOW_REQUEST 타입이면 FollowRequestPreview가 생성되어야 한다*/
    @Order(5)
    @DisplayName("5. FOLLOW_REQUEST 타입일 때 FollowRequestPreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreateFollowRequestPreview_whenTypeIsFollowRequest() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.FOLLOW_REQUEST,
          new FollowRequestMeta());
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.FOLLOW_REQUEST, FollowRequestPreview.class);
    }

    /*[Case #6] FOLLOW_ACCEPT 타입이면 FollowAcceptPreview가 생성되어야 한다*/
    @Order(6)
    @DisplayName("6. FOLLOW_ACCEPT 타입일 때 FollowAcceptPreview가 생성되는지 검증")
    @Test
    void pushNotification_shouldCreateFollowAcceptPreview_whenTypeIsFollowAccept() throws Exception {
      //given
      Notification notification = baseNotification(NotificationType.FOLLOW_ACCEPT,
          new FollowAcceptMeta());
      NotificationMessage message = capturedMessage(
          NotificationPushCommand.newCommand(notification, "sort-1")
      );

      //then
      assertPayload(message.payload(), NotificationType.FOLLOW_ACCEPT, FollowAcceptPreview.class);
    }
  }

  private void assertPayload(Payload payload, NotificationType expectedType, Class<?> previewType) {
    assertThat(payload.notificationType()).isEqualTo(expectedType);
    assertThat(payload.preview()).isInstanceOf(previewType);
    assertThat(payload.metadata()).isNotNull();
  }
}
