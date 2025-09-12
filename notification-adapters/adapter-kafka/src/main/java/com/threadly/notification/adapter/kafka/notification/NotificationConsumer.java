package com.threadly.notification.adapter.kafka.notification;

import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.in.NotificationIngestionUseCase;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Kafka 알림 이벤트 수신 consumer
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

  private final NotificationIngestionUseCase notificationIngestionUseCase;

  @Bean("notification")
  public Consumer<Message<NotificationEvent>> notificationEventConsumer() {
    return message -> {
      Object rawKey = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
      NotificationEvent event = message.getPayload();

      log.debug("Processing notification - key: {}, eventId: {}", rawKey, event.getEventId());

      // key와 receiverId 일치성 검증
      if (rawKey != null && !rawKey.equals(event.getReceiverUserId())) {
        log.warn("key 불일치 - key: {}, receiverId: {}", rawKey, event.getReceiverUserId());
      } else {
        notificationIngestionUseCase.ingest(
            new NotificationCommand(
                event.getEventId(),
                event.getReceiverUserId(),
                event.getNotificationType(),
                event.getMetadata(),
                event.getOccurredAt(),
                event.getActorProfile()
            )
        );
      }

      log.debug("Notification successfully - eventId: {}", event.getEventId());
    };
  }
}

