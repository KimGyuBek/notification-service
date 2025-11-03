package com.threadly.notification.core.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.notification.NotificationException;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import java.time.LocalDateTime;
import java.util.List;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationQueryQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

  @InjectMocks
  private NotificationQueryService notificationQueryService;

  @Mock
  private NotificationQueryPort notificationQueryPort;

  private Notification sampleNotification() {
    return Notification.newNotification(
        "event-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        new PostLikeMeta("post-1")
    );
  }

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("findNotificationDetail 테스트")
  class FindNotificationDetailTest {

    /*[Case #1] 알림 상세를 성공적으로 반환해야 한다*/
    @Order(1)
    @DisplayName("1. 알림 상세 조회가 성공적으로 수행되는지 검증")
    @Test
    void findNotificationDetail_shouldReturnResponse_whenNotificationExists() throws Exception {
      //given
      Notification notification = sampleNotification();
      when(notificationQueryPort.fetchByEventId("event-1"))
          .thenReturn(Optional.of(notification));

      //when
      GetNotificationDetailsApiResponse response =
          notificationQueryService.findNotificationDetail("receiver-1", "event-1");

      //then
      assertThat(response.eventId()).isEqualTo(notification.getEventId());
      assertThat(response.receiverId()).isEqualTo(notification.getReceiverId());
      assertThat(response.notificationType()).isEqualTo(notification.getNotificationType());
      assertThat(response.actorProfile()).isEqualTo(notification.getActorProfile());
      assertThat(response.metaData()).isEqualTo(notification.getMetadata());
      assertThat(response.isRead()).isEqualTo(notification.isRead());
    }

    /*[Case #2] eventId가 빈 값이면 INVALID_REQUEST 예외가 발생해야 한다*/
    @Order(2)
    @DisplayName("2. eventId가 비어있으면 INVALID_REQUEST 예외가 발생하는지 검증")
    @Test
    void findNotificationDetail_shouldThrow_whenEventIdEmpty() throws Exception {
      //when & then
      assertThatThrownBy(() -> notificationQueryService.findNotificationDetail("receiver-1", ""))
          .isInstanceOf(NotificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    /*[Case #3] 알림이 존재하지 않으면 NOTIFICATION_NOT_FOUND 예외가 발생해야 한다*/
    @Order(3)
    @DisplayName("3. 알림이 존재하지 않으면 NOTIFICATION_NOT_FOUND 예외가 발생하는지 검증")
    @Test
    void findNotificationDetail_shouldThrow_whenNotificationMissing() throws Exception {
      //given
      when(notificationQueryPort.fetchByEventId("event-1"))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> notificationQueryService.findNotificationDetail("receiver-1", "event-1"))
          .isInstanceOf(NotificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    /*[Case #4] 다른 사용자의 알림이면 NOTIFICATION_ACCESS_FORBIDDEN 예외가 발생해야 한다*/
    @Order(4)
    @DisplayName("4. 다른 사용자의 알림이면 NOTIFICATION_ACCESS_FORBIDDEN 예외가 발생하는지 검증")
    @Test
    void findNotificationDetail_shouldThrow_whenReceiverMismatch() throws Exception {
      //given
      when(notificationQueryPort.fetchByEventId("event-1"))
          .thenReturn(Optional.of(sampleNotification()));

      //when & then
      assertThatThrownBy(() -> notificationQueryService.findNotificationDetail("other-user", "event-1"))
          .isInstanceOf(NotificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.NOTIFICATION_ACCESS_FORBIDDEN);
    }
  }

  @Order(2)
  @Nested
  @DisplayName("findNotificationByCursor 테스트")
  class FindNotificationByCursorTest {

    /*[Case #1] 지정된 limit 기준으로 커서 응답이 생성되어야 한다*/
    @DisplayName("1. 커서 조회 시 limit 기준으로 응답이 생성되는지 검증")
    @Test
    void findNotificationByCursor_shouldReturnPagedResult() throws Exception {
      //given
      GetNotificationsQuery query = new GetNotificationsQuery("receiver-1", null, null, 2);
      List<NotificationDetails> rawResults = List.of(
          new NotificationDetails("event-1", "sort-3", "receiver-1",
              NotificationType.POST_LIKE, LocalDateTime.of(2024, 1, 1, 12, 3),
              new ActorProfile("actor-1", "행위자", "/profile.png"), false),
          new NotificationDetails("event-2", "sort-2", "receiver-1",
              NotificationType.POST_LIKE, LocalDateTime.of(2024, 1, 1, 12, 2),
              new ActorProfile("actor-2", "행위자2", "/profile2.png"), false),
          new NotificationDetails("event-3", "sort-1", "receiver-1",
              NotificationType.POST_LIKE, LocalDateTime.of(2024, 1, 1, 12, 1),
              new ActorProfile("actor-3", "행위자3", "/profile3.png"), false)
      );
      when(notificationQueryPort.fetchAllByCursor(query)).thenReturn(rawResults);

      //when
      CursorPageApiResponse<NotificationDetails> response =
          notificationQueryService.findNotificationByCursor(query);

      //then
      assertThat(response.content()).hasSize(2);
      assertThat(response.content().get(0).eventId()).isEqualTo("event-1");
      assertThat(response.nextCursor().cursorId()).isEqualTo("sort-2");
      assertThat(response.nextCursor().cursorTimestamp()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 2));
    }
  }

  @Order(3)
  @Nested
  @DisplayName("findUnreadNotificationByCursor 테스트")
  class FindUnreadNotificationByCursorTest {

    /*[Case #1] 미확인 알림 커서 조회 결과를 반환해야 한다*/
    @DisplayName("1. 미확인 알림 커서 조회 결과가 반환되는지 검증")
    @Test
    void findUnreadNotificationByCursor_shouldReturnPagedResult() throws Exception {
      //given
      GetNotificationsQuery query = new GetNotificationsQuery("receiver-1", null, null, 2);
      List<NotificationDetails> rawResults = List.of(
          new NotificationDetails("event-10", "sort-10", "receiver-1",
              NotificationType.FOLLOW, LocalDateTime.of(2024, 2, 1, 8, 30),
              new ActorProfile("actor-10", "팔로워", "/follower.png"), false)
      );
      when(notificationQueryPort.fetchUnreadByCursor(query)).thenReturn(rawResults);

      //when
      CursorPageApiResponse<NotificationDetails> response =
          notificationQueryService.findUnreadNotificationByCursor(query);

      //then
      assertThat(response.content()).hasSize(1);
      assertThat(response.content().get(0).eventId()).isEqualTo("event-10");
      assertThat(response.nextCursor().cursorId()).isNull();
      assertThat(response.nextCursor().cursorTimestamp()).isNull();
    }
  }
}
