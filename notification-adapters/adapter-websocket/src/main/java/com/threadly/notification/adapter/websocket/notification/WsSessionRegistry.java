package com.threadly.notification.adapter.websocket.notification;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
public class WsSessionRegistry {

  private final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> sessions = new ConcurrentHashMap<>();

  public void add(String userId, WebSocketSession session) {
    sessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(session);
    log.info("웹소켓 세션 추가 userId={}, sessionId={}", userId, session.getId());
  }

  public void remove(String userId, WebSocketSession session) {
    var list = sessions.get(userId);
    if (list != null) {
      list.remove(session);
      log.info("웹소켓 세션 제거 userId={}, sessionId={}", userId, session.getId());
    }
  }

  public void emit(String userId, String json) {
    var list = sessions.get(userId);
    if (list == null) {
      return;
    }

    list.removeIf(session -> {

      /*닫힌 세션 제거*/
      if (!session.isOpen()) {
        return true;
      }

      try {
        session.sendMessage(new TextMessage(json));
        return false;
      } catch (IOException e) {
        log.warn("메시지 전송 실패 userId={}, sessionId={}, error={}", userId, session.getId(),
            e.getMessage());

        /*전송 실패한 세션 제거*/
        sessions.remove(userId, session);
        return true;
      }
    });
  }

  public int activeCount(String userId) {
    List<WebSocketSession> list = sessions.get(userId);
    return list == null ? 0 : (int) list.stream().filter(WebSocketSession::isOpen).count();
  }
}
