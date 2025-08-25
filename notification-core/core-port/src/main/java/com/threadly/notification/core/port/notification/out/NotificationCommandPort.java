package com.threadly.notification.core.port.notification.out;

import com.threadly.notification.core.domain.notification.Notification;

/**
 * 게시글 좋아요 command port
 */
public interface NotificationCommandPort {

  /**
   * notification 저장
   *
   * @param notification
   */
  void saveNotification(Notification notification);

}
