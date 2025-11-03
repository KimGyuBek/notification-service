package com.threadly.notification.adapter.persistence.notification.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.adapter.persistence.notification.repository.MongoNotificationRepository;
import com.threadly.notification.adapter.persistence.notification.repository.NotificationCustomRepository;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.core.port.notification.out.dto.SavedNotificationEventDoc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationPersistenceAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class NotificationPersistenceAdapterTest {

  @InjectMocks
  private NotificationPersistenceAdapter notificationPersistenceAdapter;

  @Mock
  private MongoNotificationRepository mongoNotificationRepository;

  @Mock
  private NotificationCustomRepository notificationCustomRepository;

  private Notification sampleDomain() {
    return Notification.newNotification(
        "event-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        new PostLikeMeta("post-1")
    );
  }

  private NotificationDoc sampleDoc() {
    return NotificationDoc.newDoc(
        "event-1",
        "receiver-1",
        "sort-1",
        NotificationType.POST_LIKE,
        new PostLikeMeta("post-1"),
        LocalDateTime.of(2024, 1, 1, 12, 0),
        new ActorProfile("actor-1", "행위자", "/profile.png")
    );
  }

  @Nested
  @DisplayName("save 테스트")
  class SaveTest {

    /*[Case #1] 알림을 저장하고 SavedNotificationEventDoc이 반환되어야 한다*/
    @DisplayName("1. 알림을 저장하고 SavedNotificationEventDoc이 반환되는지 검증")
    @Test
    void save_shouldPersistNotification() throws Exception {
      //given
      Notification domain = sampleDomain();
      NotificationDoc savedDoc = sampleDoc();
      when(mongoNotificationRepository.save(any(NotificationDoc.class))).thenReturn(savedDoc);

      //when
      SavedNotificationEventDoc saved = notificationPersistenceAdapter.save(domain);

      //then
      ArgumentCaptor<NotificationDoc> docCaptor = ArgumentCaptor.forClass(NotificationDoc.class);
      verify(mongoNotificationRepository).save(docCaptor.capture());
      NotificationDoc doc = docCaptor.getValue();
      assertThat(doc.getEventId()).isEqualTo(domain.getEventId());
      assertThat(doc.getReceiverId()).isEqualTo(domain.getReceiverId());
      assertThat(saved.eventId()).isEqualTo(savedDoc.getEventId());
      assertThat(saved.sortId()).isEqualTo(savedDoc.getSortId());
      assertThat(saved.type()).isEqualTo(savedDoc.getNotificationType());
    }
  }

  @Nested
  @DisplayName("fetchByEventId 테스트")
  class FetchByEventIdTest {

    /*[Case #1] 저장된 알림을 도메인으로 변환하여 반환해야 한다*/
    @DisplayName("1. 저장된 알림을 도메인으로 변환하여 반환하는지 검증")
    @Test
    void fetchByEventId_shouldReturnDomain() throws Exception {
      //given
      NotificationDoc doc = sampleDoc();
      when(mongoNotificationRepository.findById("event-1"))
          .thenReturn(Optional.of(doc));

      //when
      Optional<Notification> result = notificationPersistenceAdapter.fetchByEventId("event-1");

      //then
      assertThat(result).isPresent();
      assertThat(result.get().getEventId()).isEqualTo(doc.getEventId());
      assertThat(result.get().getNotificationType()).isEqualTo(doc.getNotificationType());
    }
  }

  @Nested
  @DisplayName("fetchAllByCursor 테스트")
  class FetchAllByCursorTest {

    /*[Case #1] 커서 조회 결과가 NotificationDetails 리스트로 매핑되어야 한다*/
    @DisplayName("1. 커서 조회 결과가 NotificationDetails 리스트로 매핑되는지 검증")
    @Test
    void fetchAllByCursor_shouldMapDocsToDetails() throws Exception {
      //given
      GetNotificationsQuery query = new GetNotificationsQuery("receiver-1", null, null, 10);
      NotificationDoc doc = sampleDoc();
      doc.markAsRead();
      when(notificationCustomRepository.findNotificationsByCursor(query))
          .thenReturn(List.of(doc));

      //when
      List<NotificationDetails> result = notificationPersistenceAdapter.fetchAllByCursor(query);

      //then
      assertThat(result).hasSize(1);
      NotificationDetails details = result.get(0);
      assertThat(details.eventId()).isEqualTo(doc.getEventId());
      assertThat(details.isRead()).isTrue();
    }
  }

  @Nested
  @DisplayName("markAsRead 테스트")
  class MarkAsReadTest {

    /*[Case #1] 알림 읽음 상태가 업데이트되어야 한다*/
    @DisplayName("1. 알림 읽음 상태 업데이트가 수행되는지 검증")
    @Test
    void markAsRead_shouldUpdateRepository() throws Exception {
      //given
      Notification notification = sampleDomain();
      notification.markAsRead();

      //when
      notificationPersistenceAdapter.markAsRead(notification);

      //then
      verify(notificationCustomRepository)
          .updateIsReadByEventId(eq(notification.getEventId()), eq(true));
    }
  }
}
