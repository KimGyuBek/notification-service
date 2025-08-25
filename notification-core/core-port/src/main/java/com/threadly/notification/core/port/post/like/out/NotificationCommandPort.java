package com.threadly.notification.core.port.post.like.out;

import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.post.like.in.NotificationCommand;

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
