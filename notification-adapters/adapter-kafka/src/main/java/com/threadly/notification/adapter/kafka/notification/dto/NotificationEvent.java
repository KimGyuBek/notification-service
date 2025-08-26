package com.threadly.notification.adapter.kafka.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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

  public NotificationCommand toCommand() {
    return new NotificationCommand(
        this.getEventId(),
        this.getReceiverUserId(),
        this.getNotificationType(),
        this.getMetadata(),
        this.getOccurredAt(),
        this.getActorProfile()
    );
  }
}