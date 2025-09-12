package com.threadly.notification.core.port.notification.out;

import com.threadly.notification.core.port.notification.out.dto.NotificationMessage;

/**
 * Notification 알림 푸시 port
 */
public interface NotificationPushPort {

  /**
   * 주어진 userId에 해당하는 사용자에게 payload 전송
   * @param userId
   * @param payload
   */
  void pushToUser(String userId, NotificationMessage payload);

}
