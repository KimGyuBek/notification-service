package com.threadly.notification.core.port.notification.out.dto;

import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import java.time.LocalDateTime;

/**
 * 저장된 NotificationEventDoc 응답 dto
 */
public record SavedNotificationEventDoc(
    String eventId,
    String sortId,
    NotificationType type,
    NotificationMetaData metaData,
    LocalDateTime occurredAt
) {

}
