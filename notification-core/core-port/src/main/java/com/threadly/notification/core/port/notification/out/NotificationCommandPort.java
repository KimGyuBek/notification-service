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

  /**
   * 주어진 eventId에 해당하는 알림 데이터 삭제
   * @param eventId
   */
  void deleteNotificationByEventId(String eventId);

  /**
   * 주어진 receiverId에 해당하는 사용자의 알림 데이터 전체 삭제
   * @param receiverId
   */
  void deleteAllNotifications(String receiverId);
}
