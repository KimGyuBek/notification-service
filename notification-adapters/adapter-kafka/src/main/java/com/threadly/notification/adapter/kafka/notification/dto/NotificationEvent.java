package com.threadly.notification.adapter.kafka.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.notification.core.domain.notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

  /*eventId*/
  private String eventId;

  /*알림 받는 사용자*/
  private String receiverUserId;

  /*Event 종류*/
  private NotificationType notificationType;

  /*행위자 프로필*/
  private ActorProfile actorProfile;

  /*이벤트 발생 시각*/
  private LocalDateTime occurredAt;

  /*메타 데이터*/
  private Map<String, Object> metadata;

  @JsonCreator
  public NotificationEvent(
      @JsonProperty("eventId") String eventId,
      @JsonProperty("receiverUserId") String receiverUserId,
      @JsonProperty("notificationType") NotificationType notificationType,
      @JsonProperty("occurredAt") LocalDateTime occurredAt,
      @JsonProperty("actorProfile") ActorProfile actorProfile,
      @JsonProperty("metadata") Map<String, Object> metadata
  ) {
    this.eventId = eventId;
    this.receiverUserId = receiverUserId;
    this.notificationType = notificationType;
    this.occurredAt = occurredAt;
    this.actorProfile = actorProfile;
    this.metadata = metadata;
  }
}