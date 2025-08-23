package com.threadly.notification.core.port.in.post.like;

import com.threadly.notification.core.domain.post.LikeEventType;
import java.time.LocalDateTime;

/**
 * 게시글 좋아요 알림 command
 */
public record PostLikeNotificationCommand(
    String eventId,
    String likerId,
    String receiverUserId,
    LikeEventType type,
    LocalDateTime occurredAt
) {


}
