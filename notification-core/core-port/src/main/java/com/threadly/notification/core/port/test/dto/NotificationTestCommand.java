package com.threadly.notification.core.port.test.dto;

import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kafka Notification 테스트 커맨드
 *
 * @param eventId
 * @param receiverUserId
 * @param notificationType
 * @param actorProfile
 * @param occurredAt
 * @param metadata
 */
public record NotificationTestCommand(
    String eventId,
    String receiverUserId,
    NotificationType notificationType,
    ActorProfile actorProfile,
    LocalDateTime occurredAt,
    Map<String, Object> metadata
) {

}
