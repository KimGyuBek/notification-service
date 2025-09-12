package com.threadly.notification.core.port.notification.out.dto;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.port.notification.out.dto.preview.Preview;
import java.time.LocalDateTime;

/**
 * 실시간 알림 Payload 객체
 */
public record NotificationMessage(
    String eventId,
    String sortId,
    Payload payload,
    LocalDateTime occurredAt
) {

  public record Payload(
      NotificationType notificationType,
      NotificationMetaData metadata,
      Preview preview
  ) {


  }


}
