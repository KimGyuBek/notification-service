package com.threadly.notification.core.port.notification.out;

import com.threadly.notification.core.domain.notification.Notification;
import java.util.Optional;

/**
 * Notification 조회 port
 */
public interface NotificationQueryPort {


  Optional<Notification> fetchNotificationDetails(String eventId);

}
