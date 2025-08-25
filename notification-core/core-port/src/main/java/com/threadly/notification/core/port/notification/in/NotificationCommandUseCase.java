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
  void deleteNotificationByEventIdAndUserId(String eventId, String userId);

}
