package com.threadly.notification.controller;

import com.threadly.notification.auth.JwtAuthenticationUser;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.port.notification.in.NotificationCommandUseCase;
import com.threadly.notification.core.port.notification.in.NotificationQueryUseCase;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationQueryUseCase notificationQueryUseCase;
  private final NotificationCommandUseCase notificationCommandUseCase;


  /**
   * 내 전체 알림 목록 커서 기반 조회
   *
   * @param user
   * @return
   */
  @GetMapping()
  public ResponseEntity<CursorPageApiResponse> getNotifications(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit) {

    return ResponseEntity.ok().body(
        notificationQueryUseCase.findNotificationByCursor(
            new GetNotificationsQuery(
                user.getUserId(), cursorTimestamp, cursorId, limit)
        )
    );
  }

  /**
   * 읽지 않은 알림 목록 커서 기반 조회
   * @param user
   * @param cursorTimestamp
   * @param cursorId
   * @param limit
   * @return
   */
  @GetMapping("/unread")
  public ResponseEntity<CursorPageApiResponse> getUnreadNotifications(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {

    return ResponseEntity.ok().body(
        notificationQueryUseCase.findUnreadNotificationByCursor(
            new GetNotificationsQuery(
                user.getUserId(), cursorTimestamp, cursorId, limit)
        )
    );
  }


  /**
   * 주어진 eventId에 해당하는 알림 조회
   *
   * @return
   */
  @GetMapping("/{eventId}")
  public ResponseEntity<GetNotificationDetailsApiResponse> getNotificationDetail(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String eventId
  ) {
    return ResponseEntity.ok().body(
        notificationQueryUseCase.findNotificationDetail(
            user.getUserId(), eventId)
    );
  }

  /**
   * 주어진 eventId에 해당하는 알림 읽음 처리
   *
   * @return
   */
  @PatchMapping("/{eventId}/read")
  public ResponseEntity<Void> markAsRead(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String eventId
  ) {
    notificationCommandUseCase.markNotificationAsRead(eventId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  /**
   * 전체 알림 목록 읽음 처리
   *
   * @return
   */
  @PatchMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead(
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {
    notificationCommandUseCase.markAllNotificationsAsRead(user.getUserId());
    return ResponseEntity.ok().build();
  }

  /**
   * 주어진 eventId에 해당하는 알림 삭제
   *
   * @return
   */
  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteNotification(
      @AuthenticationPrincipal JwtAuthenticationUser user,
      @PathVariable String eventId
  ) {
    notificationCommandUseCase.removeNotification(eventId, user.getUserId());
    return ResponseEntity.ok().build();
  }

  /**
   * 전체 알림 목록 삭제
   *
   * @return
   */
  @DeleteMapping("")
  public ResponseEntity<Void> deleteAllNotifications(
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {
    notificationCommandUseCase.clearNotifications(user.getUserId());
    return ResponseEntity.ok().build();
  }



}
