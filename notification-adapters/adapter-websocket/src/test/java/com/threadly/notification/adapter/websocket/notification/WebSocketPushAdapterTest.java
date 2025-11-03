package com.threadly.notification.adapter.websocket.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.adapter.websocket.notification.dto.OutEvent;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage.Payload;
import com.threadly.notification.core.port.notification.out.dto.preview.PostLikePreview;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * WebSocketPushAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class WebSocketPushAdapterTest {

  @InjectMocks
  private WebSocketPushAdapter webSocketPushAdapter;

  @Mock
  private WsSessionRegistry wsSessionRegistry;

  @Mock
  private ObjectMapper objectMapper;

  private NotificationMessage sampleMessage() {
    return new NotificationMessage(
        "event-1",
        "sort-1",
        new Payload(
            NotificationType.POST_LIKE,
            new PostLikeMeta("post-1"),
            new PostLikePreview(new ActorProfile("actor-1", "행위자", "/profile.png"))
        ),
        LocalDateTime.of(2024, 1, 1, 12, 0)
    );
  }

  @Nested
  @DisplayName("pushToUser 테스트")
  class PushToUserTest {

    /*[Case #1] 직렬화가 성공하면 세션 레지스트리에 JSON이 전파되어야 한다*/
    @DisplayName("1. 직렬화가 성공하면 세션 레지스트리에 JSON이 전파되는지 검증")
    @Test
    void pushToUser_shouldEmitSerializedJson_whenSerializationSucceeds() throws Exception {
      //given
      NotificationMessage message = sampleMessage();
      when(objectMapper.writeValueAsString(any(OutEvent.class))).thenReturn("{\"payload\":1}");

      //when
      webSocketPushAdapter.pushToUser("receiver-1", message);

      //then
      verify(wsSessionRegistry).emit(eq("receiver-1"), eq("{\"payload\":1}"));
    }

    /*[Case #2] 직렬화가 실패하면 기본 JSON으로 전파되어야 한다*/
    @DisplayName("2. 직렬화가 실패하면 기본 JSON으로 전파되는지 검증")
    @Test
    void pushToUser_shouldEmitFallbackJson_whenSerializationFails() throws Exception {
      //given
      NotificationMessage message = sampleMessage();
      doThrow(new JsonProcessingException("serialize error") {
      }).when(objectMapper).writeValueAsString(any(OutEvent.class));

      //when
      webSocketPushAdapter.pushToUser("receiver-1", message);

      //then
      verify(wsSessionRegistry).emit(eq("receiver-1"), eq("{\"type\":\"NOTIFICATION\"}"));
    }
  }
}
