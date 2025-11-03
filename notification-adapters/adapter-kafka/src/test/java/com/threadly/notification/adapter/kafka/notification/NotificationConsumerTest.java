package com.threadly.notification.adapter.kafka.notification;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.in.NotificationIngestionUseCase;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * NotificationConsumer 테스트
 */
@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

  @InjectMocks
  private NotificationConsumer notificationConsumer;

  @Mock
  private NotificationIngestionUseCase notificationIngestionUseCase;

  private NotificationEvent sampleEvent() {
    return new NotificationEvent(
        "event-1",
        "receiver-1",
        NotificationType.POST_LIKE,
        new ActorProfile("actor-1", "행위자", "/profile.png"),
        LocalDateTime.of(2024, 1, 1, 12, 0),
        Map.of("postId", "post-1")
    );
  }

  private Consumer<Message<NotificationEvent>> consumer() {
    return notificationConsumer.notificationEventConsumer();
  }

  @Nested
  @DisplayName("notificationEventConsumer 테스트")
  class NotificationEventConsumerTestCases {

    /*[Case #1] key가 receiverId와 같으면 ingest가 호출되어야 한다*/
    @DisplayName("1. key가 receiverId와 같으면 ingest가 호출되는지 검증")
    @Test
    void notificationEventConsumer_shouldIngest_whenKeyMatchesReceiver() throws Exception {
      //given
      NotificationEvent event = sampleEvent();
      Message<NotificationEvent> message = MessageBuilder
          .withPayload(event)
          .setHeader(KafkaHeaders.RECEIVED_KEY, "receiver-1")
          .build();
      ArgumentCaptor<NotificationCommand> captor = ArgumentCaptor.forClass(NotificationCommand.class);

      //when
      consumer().accept(message);

      //then
      verify(notificationIngestionUseCase).ingest(captor.capture());
      NotificationCommand command = captor.getValue();
      org.assertj.core.api.Assertions.assertThat(command.eventId()).isEqualTo(event.getEventId());
      org.assertj.core.api.Assertions.assertThat(command.receiverId()).isEqualTo(event.getReceiverUserId());
      org.assertj.core.api.Assertions.assertThat(command.notificationType()).isEqualTo(event.getNotificationType());
      org.assertj.core.api.Assertions.assertThat(command.metadata()).isEqualTo(event.getMetadata());
    }

    /*[Case #2] key가 receiverId와 다르면 ingest가 호출되지 않아야 한다*/
    @DisplayName("2. key가 receiverId와 다르면 ingest가 호출되지 않는지 검증")
    @Test
    void notificationEventConsumer_shouldSkipIngest_whenKeyMismatch() throws Exception {
      //given
      NotificationEvent event = sampleEvent();
      Message<NotificationEvent> message = MessageBuilder
          .withPayload(event)
          .setHeader(KafkaHeaders.RECEIVED_KEY, "other-user")
          .build();

      //when
      consumer().accept(message);

      //then
      verify(notificationIngestionUseCase, never()).ingest(org.mockito.ArgumentMatchers.any());
    }
  }
}
