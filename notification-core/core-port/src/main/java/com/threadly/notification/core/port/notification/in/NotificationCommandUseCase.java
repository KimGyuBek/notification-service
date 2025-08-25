package com.threadly.notification.core.port.notification.in;

/**
 * 게시글 좋아요 알림 usecase
 */
public interface NotificationCommandUseCase {

  void handleNotificationEvent(NotificationCommand command);

}
