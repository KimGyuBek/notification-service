package com.threadly.notification.core.service.notification;

import com.threadly.notification.core.port.notification.out.NotificationPushPort;
import com.threadly.notification.core.port.notification.out.dto.NotificationPayload;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 실시간 알림 발행 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationPushService {

  private final NotificationPushPort notificationPushPort;

  /**
   * 주어진 userId에 해당하는 사용자에게 notification 발행
   *
   * @param command
   */
  public void pushNotification(NotificationPushCommand command) {
    notificationPushPort.pushToUser(
        command.userId(),
        new NotificationPayload(
            command.eventId(),
            command.sortId(),
            command.type(),
            command.metadata(),
            command.occurredAt()
        )
    );
  }

}
