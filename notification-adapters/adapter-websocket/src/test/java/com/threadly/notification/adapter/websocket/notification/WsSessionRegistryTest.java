package com.threadly.notification.adapter.websocket.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * WsSessionRegistry 테스트
 */
class WsSessionRegistryTest {

  private final WsSessionRegistry wsSessionRegistry = new WsSessionRegistry();

  private WebSocketSession session(String id, boolean open) {
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.getId()).thenReturn(id);
    when(session.isOpen()).thenReturn(open);
    return session;
  }

  @Nested
  @DisplayName("세션 등록/제거 테스트")
  class SessionManagementTest {

    /*[Case #1] 세션 등록과 제거가 정상적으로 동작해야 한다*/
    @DisplayName("1. 세션 등록과 제거가 정상적으로 동작하는지 검증")
    @Test
    void addAndRemove_shouldManageSessions() throws Exception {
      //given
      WebSocketSession session = session("session-1", true);

      //when
      wsSessionRegistry.add("user-1", session);

      //then
      assertThat(wsSessionRegistry.activeCount("user-1")).isEqualTo(1);

      //when
      wsSessionRegistry.remove("user-1", session);

      //then
      assertThat(wsSessionRegistry.activeCount("user-1")).isZero();
    }
  }

  @Nested
  @DisplayName("emit 테스트")
  class EmitTest {

    /*[Case #1] 열린 세션에는 메시지가 전송되어야 한다*/
    @DisplayName("1. 열린 세션에는 메시지가 전송되는지 검증")
    @Test
    void emit_shouldSendMessageToOpenSession() throws Exception {
      //given
      WebSocketSession session = session("session-1", true);
      wsSessionRegistry.add("user-1", session);

      //when
      wsSessionRegistry.emit("user-1", "{\"message\":1}");

      //then
      verify(session).sendMessage(new TextMessage("{\"message\":1}"));
    }

    /*[Case #2] 닫힌 세션은 제거되어야 한다*/
    @DisplayName("2. 닫힌 세션은 제거되는지 검증")
    @Test
    void emit_shouldRemoveClosedSession() throws Exception {
      //given
      WebSocketSession session = session("session-1", false);
      wsSessionRegistry.add("user-1", session);

      //when
      wsSessionRegistry.emit("user-1", "payload");

      //then
      verify(session, never()).sendMessage(org.mockito.ArgumentMatchers.any(TextMessage.class));
      assertThat(wsSessionRegistry.activeCount("user-1")).isZero();
    }

    /*[Case #3] 전송 중 IOException 이 발생하면 세션이 제거되어야 한다*/
    @DisplayName("3. 전송 중 IOException 발생 시 세션이 제거되는지 검증")
    @Test
    void emit_shouldRemoveSession_whenSendFails() throws Exception {
      //given
      WebSocketSession session = session("session-1", true);
      doThrow(new IOException("send fail"))
          .when(session)
          .sendMessage(org.mockito.ArgumentMatchers.any(TextMessage.class));
      wsSessionRegistry.add("user-1", session);

      //when
      wsSessionRegistry.emit("user-1", "payload");

      //then
      assertThat(wsSessionRegistry.activeCount("user-1")).isZero();
    }
  }
}
