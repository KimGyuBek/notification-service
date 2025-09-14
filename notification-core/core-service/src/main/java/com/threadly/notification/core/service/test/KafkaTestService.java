package com.threadly.notification.core.service.test;

import com.threadly.notification.core.port.test.dto.NotificationTestCommand;
import com.threadly.notification.core.port.test.in.KafkaTestUseCase;
import com.threadly.notification.core.port.test.out.KafkaTestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTestService implements KafkaTestUseCase {

  private final KafkaTestPort kafkaTestPort;

  @Override
  public void sendNotificationEvent(NotificationTestCommand notificationCommand) {
    kafkaTestPort.sendNotificationEvent(notificationCommand);

  }
}
