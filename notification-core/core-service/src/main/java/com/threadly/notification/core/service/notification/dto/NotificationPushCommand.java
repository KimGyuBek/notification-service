package com.threadly.notification.core.service.notification.dto;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.port.notification.out.dto.SavedNotificationEventDoc;
import java.time.LocalDateTime;

/**
 * Notification 발행 command 객채
 */
public record NotificationPushCommand(
    String userId,
    String eventId,
    String sortId,
    NotificationType type,
    NotificationMetaData metadata,
    LocalDateTime occurredAt
) {

  public static NotificationPushCommand newCommand(String userId, SavedNotificationEventDoc saved) {
    return new NotificationPushCommand(
        userId,
        saved.eventId(),
        saved.sortId(),
        saved.type(),
        saved.metaData(),
        saved.occurredAt()
    );
  }
}
