package com.threadly.notification.core.port.test.in;

import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.test.dto.NotificationTestCommand;

/**
 * Kafka 테스트를 위한 usecase
 */
public interface KafkaTestUseCase {

  /**
   * Notification 이벤트 발행
   * @param notificationCommand
   */
  void sendNotificationEvent(NotificationTestCommand notificationCommand);

}
