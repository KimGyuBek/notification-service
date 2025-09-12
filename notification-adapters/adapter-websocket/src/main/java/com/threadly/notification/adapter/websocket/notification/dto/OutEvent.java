package com.threadly.notification.adapter.websocket.notification.dto;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage;
import com.threadly.notification.core.port.notification.out.dto.preview.Preview;

public record OutEvent(String type, String eventId, String sortId, String occurredAt,
                       Object payload) {

  public OutEvent(NotificationMessage message) {
    this("NOTIFICATION", message.eventId(), message.sortId(), message.occurredAt().toString(),
        new Body(
            message.payload().notificationType(), message.payload().metadata(), message.payload()
            .preview()
        ));
  }

  record Body(NotificationType notificationType, NotificationMetaData metadata, Preview preview) {

  }
}
