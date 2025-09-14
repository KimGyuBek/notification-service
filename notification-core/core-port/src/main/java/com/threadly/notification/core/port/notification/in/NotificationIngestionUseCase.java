package com.threadly.notification.core.port.notification.in;

/**
 * 알림 이벤트 저장 처리 usecase
 */
public interface NotificationIngestionUseCase {

  /**
   * 알림 이벤트 저장
   *
   * @param command
   */
  void ingest(NotificationCommand command);

}
