package com.threadly.notification.core.port.notification.in.dto;

import java.time.LocalDateTime;

/**
 * Notification 목록 커서 기반 조회 쿼리 객체
 * @param userId
 * @param cursorTimestamp
 * @param cursorId
 * @param limit
 */
public record GetNotificationsQuery(
    String userId,
    LocalDateTime cursorTimestamp,
    String cursorId,
    int limit

) {

}
