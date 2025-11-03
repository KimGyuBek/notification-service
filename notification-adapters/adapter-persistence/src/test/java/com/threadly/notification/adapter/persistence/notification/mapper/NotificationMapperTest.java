package com.threadly.notification.adapter.persistence.notification.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * NotificationMapper 테스트
 */
class NotificationMapperTest {

  @Nested
  @DisplayName("toEntity 테스트")
  class ToEntityTest {

    /*[Case #1] 도메인이 엔티티로 올바르게 변환되어야 한다*/
    @DisplayName("1. 도메인이 엔티티로 올바르게 변환되는지 검증")
    @Test
    void toEntity_shouldMapDomainToDoc() throws Exception {
      //given
      Notification domain = Notification.newNotification(
          "event-1",
          "receiver-1",
          NotificationType.POST_LIKE,
          LocalDateTime.of(2024, 1, 1, 12, 0),
          new ActorProfile("actor-1", "행위자", "/profile.png"),
          new PostLikeMeta("post-1")
      );

      //when
      NotificationDoc doc = NotificationMapper.toEntity(domain);

      //then
      assertThat(doc.getEventId()).isEqualTo(domain.getEventId());
      assertThat(doc.getReceiverId()).isEqualTo(domain.getReceiverId());
      assertThat(doc.getNotificationType()).isEqualTo(domain.getNotificationType());
      assertThat(doc.getMetadata()).isEqualTo(domain.getMetadata());
      assertThat(doc.isRead()).isFalse();
    }
  }

  @Nested
  @DisplayName("toDomain 테스트")
  class ToDomainTest {

    /*[Case #1] 엔티티가 도메인으로 올바르게 변환되어야 한다*/
    @DisplayName("1. 엔티티가 도메인으로 올바르게 변환되는지 검증")
    @Test
    void toDomain_shouldMapDocToDomain() throws Exception {
      //given
      NotificationDoc doc = NotificationDoc.newDoc(
          "event-1",
          "receiver-1",
          "sort-1",
          NotificationType.POST_LIKE,
          new PostLikeMeta("post-1"),
          LocalDateTime.of(2024, 1, 1, 12, 0),
          new ActorProfile("actor-1", "행위자", "/profile.png")
      );
      doc.markAsRead();

      //when
      Notification domain = NotificationMapper.toDomain(doc);

      //then
      assertThat(domain.getEventId()).isEqualTo(doc.getEventId());
      assertThat(domain.getReceiverId()).isEqualTo(doc.getReceiverId());
      assertThat(domain.getNotificationType()).isEqualTo(doc.getNotificationType());
      assertThat(domain.getMetadata()).isEqualTo(doc.getMetadata());
      assertThat(domain.isRead()).isTrue();
    }
  }
}
