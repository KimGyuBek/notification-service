package com.threadly.notification.core.port.post.like.in;

import com.threadly.notification.core.domain.notification.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 게시글 좋아요 알림 command
 */
public record NotificationCommand(
    String eventId,
    String receiverId,
    NotificationType notificationType,
    Map<String, Object> metadata,
    LocalDateTime occurredAt
) {


}
