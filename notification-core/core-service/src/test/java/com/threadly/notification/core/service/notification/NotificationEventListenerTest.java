package com.threadly.notification.core.service.notification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationEventListener 테스트
 */
@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

  @InjectMocks
  private NotificationEventListener notificationEventListener;

  @Mock
  private NotificationDeliveryService notificationDeliveryService;

  private NotificationPushCommand sampleCommand() {
    Notification notification = Notification.newNotification(
        "event-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        new PostLikeMeta("post-1")
    );
    return NotificationPushCommand.newCommand(notification, "sort-1");
  }

  @Nested
  @DisplayName("onNotificationPublished 테스트")
  class OnNotificationPublishedTest {

    /*[Case #1] 알림 발송 서비스에 위임되어야 한다*/
    @DisplayName("1. 알림 발송 서비스에 위임되는지 검증")
    @Test
    void onNotificationPublished_shouldDelegateToDeliveryService() throws Exception {
      //given
      NotificationPushCommand command = sampleCommand();

      //when
      notificationEventListener.onNotificationPublished(command);

      //then
      verify(notificationDeliveryService).pushNotification(command);
    }

    /*[Case #2] 발송 중 예외가 발생하면 그대로 전파되어야 한다*/
    @DisplayName("2. 발송 중 예외가 발생하면 그대로 전파되는지 검증")
    @Test
    void onNotificationPublished_shouldPropagateException_whenDeliveryFails() throws Exception {
      //given
      NotificationPushCommand command = sampleCommand();
      doThrow(new IllegalStateException("delivery fail"))
          .when(notificationDeliveryService)
          .pushNotification(command);

      //when & then
      assertThatThrownBy(() -> notificationEventListener.onNotificationPublished(command))
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("delivery fail");
    }
  }
}
