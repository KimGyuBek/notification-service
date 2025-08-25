package com.threadly.notification.core.port.notification.in;

import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;

/**
 * Notification 조회 관련 usecase
 */
public interface FetchNotificationUseCase {

  /**
   * 주어진 eventId에 해당하는 알림 상세 조회
   *
   * @return
   */
  GetNotificationDetailsApiResponse getNotificationDetail(String receiverId, String eventId);


}
