package com.threadly.notification.adapter.websocket.notification;

import static com.threadly.notification.adapter.websocket.notification.dto.WsInboundMessage.InboundMessageType.ACK;
import static com.threadly.notification.adapter.websocket.notification.dto.WsInboundMessage.InboundMessageType.RESYNC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.adapter.websocket.notification.dto.AckRequest;
import com.threadly.notification.adapter.websocket.notification.dto.WsInboundMessage;
import com.threadly.notification.adapter.websocket.notification.dto.WsInboundMessage.InboundMessageType;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler extends TextWebSocketHandler {

  private final WsSessionRegistry wsSessionRegistry;
  private final ObjectMapper objectMapper;

  private final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    try {
      String userId = getUserId(session);

      wsSessionRegistry.add(userId, session);

      /*하트 비트 */
      startHeartbeat(session);

      log.info("웹소켓 연결 성공 userId={}, sessionId={}", userId, session.getId());
    } catch (Exception e) {
      log.error("웹소켓 연결 실패 sessionId={}, error={}", session.getId(), e.getMessage());
      session.close(CloseStatus.BAD_DATA);
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String userId = getUserId(session);
    if (userId == null) {
      return;
    }

    handleInbound(userId, message.getPayload());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String userId = getUserId(session);
    wsSessionRegistry.remove(userId, session);
    log.info("웹소켓 연결 종료 userId={}, sessionId={}, status={}", userId, session.getId(), status);
  }


  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    String userId = getUserId(session);

    log.error("웹소켓 전송 에러 userId={}, sessionId={}, error={}", userId, session.getId(),
        exception.getMessage());

    if (session.isOpen()) {
      session.close(CloseStatus.SERVER_ERROR.withReason("Transport Error"));
    }

    wsSessionRegistry.remove(userId, session);
  }

  private void handleInbound(String userId, String message) {
    try {
      WsInboundMessage obj = objectMapper.readValue(message, WsInboundMessage.class);

      switch(obj.type()) {
        case ACK -> {
          log.debug("ACK 메시지 수신 userId={}, message={}", userId, obj.toString());

          // TODO last receivedId 반영하고 싶으면 저장소에 업데이트
        }
        case RESYNC -> {
          log.debug("RESYNC 메시지 수신 userId={}, message={}", userId, obj.toString());

          // TODO 재동기화 로직 구현
        }
        default -> {
          log.error("지원하지 않는 메세지 type={}", obj.type());
        }
      }

    } catch (Exception e) {
      log.warn("인바운드 메시지 처리 실패 userId={}, message={}, error={}", userId, message, e.getMessage());
    }
  }



  private void startHeartbeat(WebSocketSession session) {
    heartbeatExecutor.scheduleAtFixedRate(() -> {
      if (session.isOpen()) {
        try {
          session.sendMessage(new TextMessage("{\"type\":\"heartbeat\"}"));
        } catch (IOException e) {
          log.warn("하트비트 전송 실패 sessionId={}", session.getId());
        }
      }
    }, 15, 15, TimeUnit.SECONDS);
  }

  /**
   * session attribute에서 userId 추출
   *
   * @param session
   * @return
   */
  private static String getUserId(WebSocketSession session) {
    return (String) session.getAttributes().get("userId");
  }
}
