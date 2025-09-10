package com.threadly.notification.adapter.websocket.notification.dto;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.port.notification.out.dto.NotificationPayload;

public record OutEvent(String type, String eventId,String sortId, String occurredAt, Object payload) {

  public OutEvent(NotificationPayload payload) {
    this("NOTIFICATION", payload.eventId(),payload.sortId(), payload.occurredAt().toString(), new Body(
        payload.type(), payload.metaData()
    ));
  }

  record Body(NotificationType notificationType, NotificationMetaData metadata) {

  }
}
