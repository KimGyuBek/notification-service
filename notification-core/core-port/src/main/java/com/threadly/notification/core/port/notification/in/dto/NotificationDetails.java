package com.threadly.notification.core.port.notification.in.dto;

import com.threadly.notification.commons.response.CursorSupport;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import java.time.LocalDateTime;

/**
 * 알림 조회 응답 APi DTO
 */
public record NotificationDetails(
    String eventId,
    String receiverId,
    NotificationType notificationType,
    LocalDateTime occurredAt,
    ActorProfile actorProfile,
    boolean isRead
) implements CursorSupport {

  @Override
  public LocalDateTime cursorTimeStamp() {
    return occurredAt;
  }

  @Override
  public String cursorId() {
    return eventId;
  }
}
