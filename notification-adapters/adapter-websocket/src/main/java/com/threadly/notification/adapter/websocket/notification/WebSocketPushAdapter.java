package com.threadly.notification.adapter.websocket.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.adapter.websocket.notification.dto.OutEvent;
import com.threadly.notification.core.port.notification.out.NotificationPushPort;
import com.threadly.notification.core.port.notification.out.dto.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketPushAdapter implements NotificationPushPort {

  private final WsSessionRegistry wsSessionRegistry;
  private final ObjectMapper objectMapper;

  @Override
  public void pushToUser(String userId, NotificationPayload payload) {
    String json = toOutboundJson(payload);

    wsSessionRegistry.emit(userId, json);
  }

  private String toOutboundJson(NotificationPayload payload) {
    try {
      return objectMapper.writeValueAsString(new OutEvent(payload));
    } catch (Exception e) {
      return "{\"type\":\"NOTIFICATION\"}";
    }
  }

}
