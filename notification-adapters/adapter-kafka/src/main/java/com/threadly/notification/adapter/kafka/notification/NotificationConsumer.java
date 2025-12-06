package com.threadly.notification.adapter.kafka.notification;

import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.in.NotificationIngestionUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Kafka 알림 이벤트 수신 consumer
 */
@Component
@Slf4j
public class NotificationConsumer {

  private final NotificationIngestionUseCase notificationIngestionUseCase;
  private final Counter retryAttemptCounter;
  private final Counter consumeSuccessCounter;


  public NotificationConsumer(NotificationIngestionUseCase notificationIngestionUseCase,
      MeterRegistry meterRegistry) {

    this.notificationIngestionUseCase = notificationIngestionUseCase;
    this.retryAttemptCounter = Counter.builder(
            "notification_consumer_retry_attempt")
        .tag("binding", "notification-in-0")
        .description("NotificationConsumer 재시도 횟수")
        .register(meterRegistry);
    this.consumeSuccessCounter = Counter.builder("notification_consumer_success")
        .tag("binding", "notification-in-0")
        .description("정상 처리된 알림 수 ")
        .register(meterRegistry);
  }

  @Bean("notification")
  public Consumer<Message<NotificationEvent>> notificationEventConsumer() {
    return message -> {
      Object rawKey = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
      NotificationEvent event = message.getPayload();

      AtomicInteger attempt = message.getHeaders()
          .get(IntegrationMessageHeaderAccessor.DELIVERY_ATTEMPT, AtomicInteger.class);
      int attemptValue = attempt != null ? attempt.get() : 1;

      /*재시도인 경우*/
      if (attemptValue > 1) {
        retryAttemptCounter.increment();
        log.warn("알림 재시도 처리 중({}): eventId={}, attempt={}", attemptValue, event.getEventId(), attempt);
      }

      log.debug("Processing notification - key: {}, eventId: {}", rawKey, event.getEventId());

      /*key와 receiverId가 불일치하는 경우*/
      if (rawKey != null && !rawKey.equals(event.getReceiverUserId())) {
        log.warn("key 불일치 - key: {}, receiverId: {}", rawKey, event.getReceiverUserId());
        return;
      }

      try {
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
        consumeSuccessCounter.increment();
        log.info("알림 처리 성공: eventId={}", event.getEventId());

      } catch (Exception e) {
        log.warn("알림 처리 실패(재시도 예정): eventId={}", event.getEventId(), e);
        throw e;
      }
    };
  }
}

