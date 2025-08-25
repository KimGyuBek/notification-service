package com.threadly.notification.controller;

import com.threadly.notification.auth.JwtAuthenticationUser;
import com.threadly.notification.core.port.notification.in.FetchNotificationUseCase;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final FetchNotificationUseCase fetchNotificationUseCase;


  /**
   * 내 알림 목록 커서 기반 조회
   *
   * @param user
   * @return
   */
  @GetMapping()
  public ResponseEntity<Void> getNotifications(
      @AuthenticationPrincipal JwtAuthenticationUser user
  ) {

    return ResponseEntity.ok().build();
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
        fetchNotificationUseCase.getNotificationDetail(
            user.getUserId(), eventId)
    );
  }

  /**
   * 주어진 eventId에 해당하는 알림 읽음 처리
   *
   * @return
   */
  @PatchMapping("/{eventId}/read")
  public ResponseEntity<Void> markAsRead() {

    return ResponseEntity.ok().build();
  }

  /**
   * 전체 알림 목록 읽음 처리
   *
   * @return
   */
  @PatchMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead() {
    return ResponseEntity.ok().build();
  }


  /**
   * 주어진 eventId에 해당하는 알림 삭제
   *
   * @return
   */
  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteNotification() {

    return ResponseEntity.ok().build();
  }

  /**
   * 전체 알림 목록 삭제
   *
   * @return
   */
  @DeleteMapping("")
  public ResponseEntity<Void> deleteAllNotifications() {

    return ResponseEntity.ok().build();
  }

  /**
   * test
   *
   * @return
   */
  @GetMapping("/test")
  public ResponseEntity<Void> test() {
    return ResponseEntity.ok().build();
  }


}
