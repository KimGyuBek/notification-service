package com.threadly.notification.core.port.notification.in;

import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;

/**
 * Notification 조회 관련 usecase
 */
public interface NotificationQueryUseCase {

  /**
   * 주어진 eventId에 해당하는 알림 상세 조회
   *
   * @return
   */
  GetNotificationDetailsApiResponse findNotificationDetail(String receiverId, String eventId);

  /**
   * Notification 목록 커서 기반 조회
   *
   * @param query
   * @return
   */
  CursorPageApiResponse findNotificationByCursor(GetNotificationsQuery query);

  /**
   * 읽지 않은 알림 목록 커서 기반 조회
   * @param query
   * @return
   */
  CursorPageApiResponse findUnreadNotificationByCursor(GetNotificationsQuery query);


}
