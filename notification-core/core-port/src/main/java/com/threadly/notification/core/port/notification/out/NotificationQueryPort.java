package com.threadly.notification.core.port.notification.out;

import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import java.util.List;
import java.util.Optional;

/**
 * Notification 조회 port
 */
public interface NotificationQueryPort {


  /**
   * 주어진 eventId에 해당하는 notification의 상세 정보 조회
   *
   * @param eventId
   * @return
   */
  Optional<Notification> fetchNotificationDetails(String eventId);

  /**
   * 주어진 query에 해당하는 Notification 목록 커서 기반 조회
   *
   * @param query
   * @return
   */
  List<NotificationDetails> fetchNotificationsByCursor(GetNotificationsQuery query);

  /**
   * 주어진 eventId에 해당하는 알림 데이터가 존재하는지 검증
   * @param eventId
   * @return
   */
  boolean existsNotificationByEventIdAndReceiverId(String eventId, String receiverId);
}
