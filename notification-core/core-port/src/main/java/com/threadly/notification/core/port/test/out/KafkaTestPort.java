package com.threadly.notification.core.port.test.out;

import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.test.dto.NotificationTestCommand;

/**
 * Kafka 테스트 port
 */
public interface KafkaTestPort {

  /**
   * 알림 이벤트 전송
   * @param command
   */
  void sendNotificationEvent(NotificationTestCommand command);

}
