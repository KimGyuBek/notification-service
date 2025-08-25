package com.threadly.notification.core.port.notification.in;

/**
 * 게시글 좋아요 알림 usecase
 */
public interface NotificationCommandUseCase {

  /**
   * usecase 이동 고려
   * @param command
   */
  void handleNotificationEvent(NotificationCommand command);

  /**
   * 주어진 eventId에 해당하는 알림 데이터 삭제
   * @param eventId
   */
  void removeNotification(String eventId, String userId);

  /**
   * 주어진 userId에 해당하는 사용자의 알림 데이터 전체 삭제
   * @param userId
   */
  void clearNotifications(String userId);

  /**
   * 주어진 eventId, userId에 해당하는 알림의 읽음 처리
   * @param eventId
   * @param userId
   */
  void markNotificationAsRead(String eventId, String userId);

  /**
   * 주어진 userId에 해당하는 알림 데이터 전체 읽음 처리
   * @param userId
   */
  void markAllNotificationsAsRead(String userId);

}
