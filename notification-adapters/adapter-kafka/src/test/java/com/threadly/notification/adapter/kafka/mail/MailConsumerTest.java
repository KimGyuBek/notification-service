package com.threadly.notification.adapter.kafka.mail;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.threadly.notification.adapter.kafka.mail.dto.MailEvent;
import com.threadly.notification.core.domain.mail.MailType;
import com.threadly.notification.core.port.mail.in.SendMailUseCase;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
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
 * MailConsumer 테스트
 */
@ExtendWith(MockitoExtension.class)
class MailConsumerTest {

  @InjectMocks
  private MailConsumer mailConsumer;

  @Mock
  private SendMailUseCase sendMailUseCase;

  private MailEvent sampleEvent() {
    return new MailEvent(
        "event-1",
        MailType.VERIFICATION,
        "user@threadly.io",
        Map.of("userName", "홍길동")
    );
  }

  private Consumer<Message<MailEvent>> consumer() {
    return mailConsumer.mailEventConsumer();
  }

  @Nested
  @DisplayName("mailEventConsumer 테스트")
  class MailEventConsumerTestCases {

    /*[Case #1] key가 수신자와 일치하면 메일 전송이 수행되어야 한다*/
    @DisplayName("1. key가 수신자와 일치하면 메일 전송이 수행되는지 검증")
    @Test
    void mailEventConsumer_shouldSendMail_whenKeyMatchesReceiver() throws Exception {
      //given
      MailEvent event = sampleEvent();
      Message<MailEvent> message = MessageBuilder
          .withPayload(event)
          .setHeader(KafkaHeaders.RECEIVED_KEY, "user@threadly.io")
          .build();
      ArgumentCaptor<SendMailCommand> captor = ArgumentCaptor.forClass(SendMailCommand.class);

      //when
      consumer().accept(message);

      //then
      verify(sendMailUseCase).send(captor.capture());
      SendMailCommand command = captor.getValue();
      org.assertj.core.api.Assertions.assertThat(command.mailType()).isEqualTo(event.mailType());
      org.assertj.core.api.Assertions.assertThat(command.to()).isEqualTo(event.to());
      org.assertj.core.api.Assertions.assertThat(command.model()).isEqualTo(event.model());
    }

    /*[Case #2] key가 수신자와 다르면 메일 전송이 호출되지 않아야 한다*/
    @DisplayName("2. key가 수신자와 다르면 메일 전송이 호출되지 않는지 검증")
    @Test
    void mailEventConsumer_shouldSkipMail_whenKeyMismatch() throws Exception {
      //given
      MailEvent event = sampleEvent();
      Message<MailEvent> message = MessageBuilder
          .withPayload(event)
          .setHeader(KafkaHeaders.RECEIVED_KEY, "other@threadly.io")
          .build();

      //when
      consumer().accept(message);

      //then
      verify(sendMailUseCase, never()).send(org.mockito.ArgumentMatchers.any());
    }
  }
}
