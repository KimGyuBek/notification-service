package com.threadly.notification.adapter.kafka.mail;

import com.threadly.notification.adapter.kafka.mail.dto.MailEvent;
import com.threadly.notification.adapter.kafka.utils.KafkaConsumerLogUtils;
import com.threadly.notification.adapter.kafka.utils.RetryAttemptUtils;
import com.threadly.notification.core.port.mail.in.SendMailUseCase;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Kafka 메일 이벤트 수신 consumer
 */
@Component
@Slf4j
public class MailConsumer {

  private final SendMailUseCase sendMailUseCase;
  private final Counter retryAttemptCounter;
  private final Counter consumeSuccessCounter;

  private static final String TOPIC = "Mail";

  public MailConsumer(SendMailUseCase sendMailUseCase, MeterRegistry meterRegistry) {
    this.sendMailUseCase = sendMailUseCase;
    this.retryAttemptCounter = Counter.builder("mail_consumer_retry_attempt")
        .tag("binding", "mail-in-0")
        .description("MailConsumer 재시도 횟수")
        .register(meterRegistry);
    this.consumeSuccessCounter = Counter.builder("mail_consumer_success")
        .tag("binding", "mail-in-0")
        .description("정상 처리된 메일 수")
        .register(meterRegistry);
  }

  @Bean("mail")
  public Consumer<Message<MailEvent>> mailEventConsumer() {
    return message -> {
      Object rawKey = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
      MailEvent event = message.getPayload();

      int attempt = RetryAttemptUtils.getAttemptValue(message);

      if (attempt > 1) {
        retryAttemptCounter.increment();
        KafkaConsumerLogUtils.logRetry(TOPIC, attempt, event.eventId());
      }

      /*key 검증*/
      if (rawKey != null && !rawKey.equals(event.to())) {
        log.warn("key 불일치 - key: {}, receiverId: {}", rawKey, event.to());
        return;
      }

      try {
        sendMailUseCase.send(event.toCommand());
        KafkaConsumerLogUtils.logSuccess(TOPIC, event.eventId());

      } catch (Exception e) {
        KafkaConsumerLogUtils.logFailure(TOPIC, event.eventId(), e);
        throw e;
      }
    };

  }
}
