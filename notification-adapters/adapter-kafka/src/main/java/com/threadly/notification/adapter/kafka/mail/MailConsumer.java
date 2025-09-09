package com.threadly.notification.adapter.kafka.mail;

import com.threadly.notification.adapter.kafka.mail.dto.MailEvent;
import com.threadly.notification.core.port.mail.in.SendMailUseCase;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Kafka 메일 이벤트 수신 consumer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailConsumer {

  private final SendMailUseCase sendMailUseCase;

  @Bean("mail")
  public Consumer<Message<MailEvent>> mailEventConsumer() {
    return message -> {
      Object rawKey = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
      MailEvent event = message.getPayload();

      log.debug("Processing mail - key: {}, eventId: {}", rawKey, event.eventId());

      /*key 검증*/
      if (rawKey != null && !rawKey.equals(event.to())) {
        log.warn("key 불일치 - key: {}, receiverId: {}", rawKey, event.to());
      } else {
        sendMailUseCase.send(event.toCommand());
      }
    };

  }
}
