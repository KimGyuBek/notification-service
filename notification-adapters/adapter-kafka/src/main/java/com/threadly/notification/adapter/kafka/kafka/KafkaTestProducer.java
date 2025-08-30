package com.threadly.notification.adapter.kafka.kafka;

import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.core.port.test.dto.NotificationTestCommand;
import com.threadly.notification.core.port.test.out.KafkaTestPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 테스트 producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTestProducer implements KafkaTestPort {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Override
  public void sendNotificationEvent(NotificationTestCommand notificationCommand) {
    // NotificationTestCommand를 NotificationEvent로 변환
    NotificationEvent event = new NotificationEvent(
        notificationCommand.eventId(),
        notificationCommand.receiverUserId(),
        notificationCommand.notificationType(),
        notificationCommand.occurredAt(),
        notificationCommand.actorProfile(),
        notificationCommand.metadata()
    );

    log.info("Sending notification event: {}", event);

    // Kafka로 이벤트 전송
    kafkaTemplate.send("notification", event.getReceiverUserId(), event)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            log.info("Successfully sent notification event: {}", event.getEventId());
          } else {
            log.error("Failed to send notification event: {}", event.getEventId(), ex);
          }
        });
  }
}
