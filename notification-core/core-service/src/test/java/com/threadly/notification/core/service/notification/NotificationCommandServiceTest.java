package com.threadly.notification.core.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.notification.NotificationException;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import com.threadly.notification.core.port.notification.out.dto.SavedNotificationEventDoc;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import com.threadly.notification.core.service.utils.MetadataMapper;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * NotificationCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

  @InjectMocks
  private NotificationCommandService notificationCommandService;

  @Mock
  private NotificationCommandPort notificationCommandPort;

  @Mock
  private NotificationQueryPort notificationQueryPort;

  @Mock
  private MetadataMapper metadataMapper;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  private NotificationCommand sampleCommand() {
    return new NotificationCommand(
        "event-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        Map.of("postId", "post-1"),
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png")
    );
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("ingest 테스트")
  class IngestTest {

    /*[Case #1] 알림을 저장하고 발행 이벤트를 전파해야 한다*/
    @Order(1)
    @DisplayName("1. 알림을 저장하고 발행 이벤트가 전파되는지 검증")
    @Test
    void ingest_shouldSaveAndPublishNotification() throws Exception {
      //given
      NotificationCommand command = sampleCommand();
      NotificationMetaData metadata = new PostLikeMeta("post-1");
      when(metadataMapper.toTypeMeta(command.notificationType(), command.metadata()))
          .thenReturn(metadata);
      when(notificationCommandPort.save(any(Notification.class)))
          .thenReturn(new SavedNotificationEventDoc(
              command.eventId(),
              "sort-123",
              command.notificationType(),
              metadata,
              command.occurredAt()
          ));

      ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
      ArgumentCaptor<NotificationPushCommand> pushCommandCaptor =
          ArgumentCaptor.forClass(NotificationPushCommand.class);

      //when
      notificationCommandService.ingest(command);

      //then
      verify(metadataMapper).toTypeMeta(command.notificationType(), command.metadata());
      verify(notificationCommandPort).save(notificationCaptor.capture());
      Notification savedNotification = notificationCaptor.getValue();
      assertThat(savedNotification.getEventId()).isEqualTo(command.eventId());
      assertThat(savedNotification.getReceiverId()).isEqualTo(command.receiverId());
      assertThat(savedNotification.getNotificationType()).isEqualTo(command.notificationType());
      assertThat(savedNotification.getMetadata()).isEqualTo(metadata);
      assertThat(savedNotification.getActorProfile().getUserId()).isEqualTo("actor-1");
      assertThat(savedNotification.isRead()).isFalse();

      verify(applicationEventPublisher).publishEvent(pushCommandCaptor.capture());
      NotificationPushCommand publishedCommand = pushCommandCaptor.getValue();
      assertThat(publishedCommand.notification()).isEqualTo(savedNotification);
      assertThat(publishedCommand.sortId()).isEqualTo("sort-123");
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("removeNotification 테스트")
  class RemoveNotificationTest {

    /*[Case #1] 알림이 존재하면 삭제가 수행되어야 한다*/
    @Order(1)
    @DisplayName("1. 알림이 존재하면 삭제가 수행되는지 검증")
    @Test
    void removeNotification_shouldDelete_whenNotificationExists() throws Exception {
      //given
      when(notificationQueryPort.existsByEventIdAndReceiverId("event-1", "receiver-1"))
          .thenReturn(true);

      //when
      notificationCommandService.removeNotification("event-1", "receiver-1");

      //then
      verify(notificationQueryPort).existsByEventIdAndReceiverId("event-1", "receiver-1");
      verify(notificationCommandPort).deleteByEventId("event-1");
    }

    /*[Case #2] 알림이 존재하지 않으면 예외가 발생해야 한다*/
    @Order(2)
    @DisplayName("2. 알림이 존재하지 않으면 예외가 발생하는지 검증")
    @Test
    void removeNotification_shouldThrow_whenNotificationNotFound() throws Exception {
      //given
      when(notificationQueryPort.existsByEventIdAndReceiverId("event-1", "receiver-1"))
          .thenReturn(false);

      //when & then
      assertThatThrownBy(() -> notificationCommandService.removeNotification("event-1", "receiver-1"))
          .isInstanceOf(NotificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);

      verify(notificationCommandPort, never()).deleteByEventId(any());
    }
  }

  @Order(3)
  @Nested
  @DisplayName("clearNotifications 테스트")
  class ClearNotificationsTest {

    /*[Case #1] 사용자 알림 전체가 삭제되어야 한다*/
    @DisplayName("1. 사용자 알림 전체 삭제가 수행되는지 검증")
    @Test
    void clearNotifications_shouldDeleteAllByReceiver() throws Exception {
      //given
      String receiverId = "receiver-1";

      //when
      notificationCommandService.clearNotifications(receiverId);

      //then
      verify(notificationCommandPort).deleteAllByReceiverId(receiverId);
    }
  }

  @Order(4)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("markNotificationAsRead 테스트")
  class MarkNotificationAsReadTest {

    /*[Case #1] 알림을 읽음 처리하고 저장해야 한다*/
    @Order(1)
    @DisplayName("1. 알림을 읽음 처리한 뒤 저장하는지 검증")
    @Test
    void markNotificationAsRead_shouldUpdateNotification() throws Exception {
      //given
      Notification notification = Notification.newNotification(
          "event-1",
          "receiver-1",
          NotificationType.POST_LIKE,
          LocalDateTime.of(2024, 1, 1, 12, 0),
          new ActorProfile("actor-1", "행위자", "/profile.png"),
          new PostLikeMeta("post-1")
      );
      when(notificationQueryPort.fetchByEventIdAndReceiverId("event-1", "receiver-1"))
          .thenReturn(Optional.of(notification));

      ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

      //when
      notificationCommandService.markNotificationAsRead("event-1", "receiver-1");

      //then
      assertThat(notification.isRead()).isTrue();
      verify(notificationCommandPort).markAsRead(notificationCaptor.capture());
      assertThat(notificationCaptor.getValue().isRead()).isTrue();
    }

    /*[Case #2] 알림이 없으면 예외가 발생해야 한다*/
    @Order(2)
    @DisplayName("2. 알림이 존재하지 않으면 예외가 발생하는지 검증")
    @Test
    void markNotificationAsRead_shouldThrow_whenNotificationMissing() throws Exception {
      //given
      when(notificationQueryPort.fetchByEventIdAndReceiverId("event-1", "receiver-1"))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> notificationCommandService.markNotificationAsRead("event-1", "receiver-1"))
          .isInstanceOf(NotificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);

      verify(notificationCommandPort, never()).markAsRead(any(Notification.class));
    }
  }

  @Order(5)
  @Nested
  @DisplayName("markAllNotificationsAsRead 테스트")
  class MarkAllNotificationsAsReadTest {

    /*[Case #1] 모든 알림을 읽음 처리해야 한다*/
    @DisplayName("1. 사용자 알림 전체 읽음 처리가 수행되는지 검증")
    @Test
    void markAllNotificationsAsRead_shouldDelegateToPort() throws Exception {
      //given
      String receiverId = "receiver-1";

      //when
      notificationCommandService.markAllNotificationsAsRead(receiverId);

      //then
      verify(notificationCommandPort).markAllAsRead(eq(receiverId));
    }
  }
}
