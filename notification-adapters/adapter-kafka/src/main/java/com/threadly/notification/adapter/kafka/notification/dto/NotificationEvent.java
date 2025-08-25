package com.threadly.notification.adapter.kafka.notification.dto;

import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
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