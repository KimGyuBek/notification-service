package com.threadly.notification.adapter.kafka.notification;

import static com.threadly.notification.adapter.kafka.utils.KafkaConsumerLogUtils.logFinalFailure;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.ErrorMessage;

/**
 * Kafka 에러 핸들러
 */
@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

  /**
   * NotificationConsumer 에러 핸들러
   *
   * @param meterRegistry
   * @return
   */
  @Bean
  public Consumer<ErrorMessage> notificationErrorHandler(MeterRegistry meterRegistry) {
    Counter finalFailureCounter = Counter.builder("notification_consumer_final_failure")
        .tag("binding", "notification-in-0")
        .description("모든 재시도 이후에도 실패한 알림 수")
        .register(meterRegistry);

    return errorMessage -> {
      finalFailureCounter.increment();

      logFinalFailure(errorMessage);
    };
  }


  /**
   * MailConsumer 에러 핸들러
   *
   * @param meterRegistry
   * @return
   */
  @Bean
  public Consumer<ErrorMessage> mailErrorHandler(MeterRegistry meterRegistry) {
    Counter finalFailureCounter = Counter.builder("mail_consumer_final_failure")
        .tag("binding", "mail-in-0")
        .description("모든 재시도 이후에도 실패한 알림 수")
        .register(meterRegistry);

    return errorMessage -> {
      finalFailureCounter.increment();
      logFinalFailure(errorMessage);
    };
  }
}
