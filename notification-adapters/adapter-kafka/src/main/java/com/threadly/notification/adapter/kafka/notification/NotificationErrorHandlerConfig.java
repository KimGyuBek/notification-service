package com.threadly.notification.adapter.kafka.notification;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;

/**
 * 알림 에러 핸들러
 */
@Configuration
@Slf4j
public class NotificationErrorHandlerConfig {

  @Bean
  public Consumer<ErrorMessage> notificationErrorHandler(MeterRegistry meterRegistry) {
    Counter finalFailureCounter = Counter.builder("notification_consumer_final_failure")
        .tag("binding", "notification-in-0")
        .description("모든 재시도 이후에도 실패한 알림 수")
        .register(meterRegistry);

    return errorMessage -> {
      finalFailureCounter.increment();

      MessagingException ex = (MessagingException) errorMessage.getPayload();
      Message<?> failedMessage = ex.getFailedMessage();

      Object topic = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
      Object partition = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION);
      Object offset = failedMessage.getHeaders().get(KafkaHeaders.OFFSET);
      Object key = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
      Object payload = failedMessage.getPayload();

      log.error(
          "알림 처리 최종 실패: topic={}, partition={}, offset={}, key={}, payload={}",
          topic, partition, offset, key, payload, ex
      );
    };
  }
}
