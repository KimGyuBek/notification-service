package com.threadly.notification.core.port.notification.out.dto;

/**
 * 실시간 알림 Payload 객체
 */
public record NotificationPayload(
    String eventId,
    String type,
    String message,
    String createdAt
) {

}
