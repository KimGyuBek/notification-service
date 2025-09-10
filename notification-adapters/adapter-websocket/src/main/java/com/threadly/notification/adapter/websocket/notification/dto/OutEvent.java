package com.threadly.notification.adapter.websocket.notification.dto;

import com.threadly.notification.core.port.notification.out.dto.NotificationPayload;

public record OutEvent(String type, String eventId, String createdAt, Object payload) {

  public OutEvent(NotificationPayload payload) {
    this("NOTIFICATION", payload.eventId(), payload.createdAt().toString(), new Body(
        payload.type(), payload.message()
    ));
  }

  record Body(String kind, String message) {

  }
}
