package com.threadly.notification.controller.api;

import com.threadly.notification.auth.JwtAuthenticationUser;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림 API", description = "사용자 알림 관련 API")
public interface NotificationApi {

  /**
   * 내 전체 알림 목록 커서 기반 조회
   */
  @Operation(summary = "전체 알림 목록 커서 기반 조회", 
             description = "사용자의 모든 알림을 커서 기반 페이지네이션으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CursorPageApiResponse.class),
              examples = @ExampleObject(
                  name = "알림 목록 응답 예시",
                  value = """
                      {
                        "content": [
                          {
                            "eventId": "event-12345",
                            "sortId": "sort-98765",
                            "receiverId": "user123",
                            "notificationType": "POST_LIKE",
                            "occurredAt": "2024-01-15T10:30:00",
                            "actorProfile": {
                              "userId": "actor-user-456",
                              "nickname": "좋아요누른사용자",
                              "profileImageUrl": "https://example.com/profile/456.jpg"
                            },
                            "isRead": false
                          }
                        ],
                        "nextCursor": {
                          "cursorTimestamp": "2024-01-15T10:29:00",
                          "cursorId": "sort-98764"
                        }
                      }
                      """
              )
          )),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
  })
  @GetMapping
  ResponseEntity<CursorPageApiResponse<NotificationDetails>> getNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "커서 타임스탬프 (이전 페이지의 마지막 항목 시간)", example = "2024-01-01T10:00:00")
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @Parameter(description = "커서 ID (이전 페이지의 마지막 항목 ID)", example = "notification-123")
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @Parameter(description = "한 페이지당 조회할 알림 개수", example = "10")
      @RequestParam(value = "limit", defaultValue = "10") int limit);

  /**
   * 읽지 않은 알림 목록 커서 기반 조회
   */
  @Operation(summary = "읽지 않은 알림 목록 커서 기반 조회", 
             description = "사용자의 읽지 않은 알림만을 커서 기반 페이지네이션으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "읽지 않은 알림 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CursorPageApiResponse.class),
              examples = @ExampleObject(
                  name = "읽지 않은 알림 목록 응답 예시",
                  value = """
                      {
                        "content": [
                          {
                            "eventId": "unread-event-12345",
                            "sortId": "sort-98765",
                            "receiverId": "user123",
                            "notificationType": "POST_LIKE",
                            "occurredAt": "2024-01-15T10:30:00",
                            "actorProfile": {
                              "userId": "actor-user-456",
                              "nickname": "새로운좋아요",
                              "profileImageUrl": "https://example.com/profile/456.jpg"
                            },
                            "isRead": false
                          }
                        ],
                        "nextCursor": {
                          "cursorTimestamp": "2024-01-15T10:29:00",
                          "cursorId": "sort-98764"
                        }
                      }
                      """
              )
          )),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
  })
  @GetMapping("/unread")
  ResponseEntity<CursorPageApiResponse<NotificationDetails>> getUnreadNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "커서 타임스탬프 (이전 페이지의 마지막 항목 시간)", example = "2024-01-01T10:00:00")
      @RequestParam(value = "cursor_timestamp", required = false) LocalDateTime cursorTimestamp,
      @Parameter(description = "커서 ID (이전 페이지의 마지막 항목 ID)", example = "notification-123")
      @RequestParam(value = "cursor_id", required = false) String cursorId,
      @Parameter(description = "한 페이지당 조회할 알림 개수", example = "10")
      @RequestParam(value = "limit", defaultValue = "10") int limit
  );


  /**
   * 주어진 eventId에 해당하는 알림 조회
   */
  @Operation(summary = "알림 상세 조회", 
             description = "특정 eventId에 해당하는 알림의 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 상세 조회 성공",
          content = @Content(schema = @Schema(implementation = GetNotificationDetailsApiResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "403", description = "해당 알림에 대한 접근 권한 없음", content = @Content),
      @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
  })
  @GetMapping("/{eventId}")
  ResponseEntity<GetNotificationDetailsApiResponse> getNotificationDetail(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "알림 이벤트 ID", example = "event-12345", required = true)
      @PathVariable String eventId
  );

  /**
   * 주어진 eventId에 해당하는 알림 읽음 처리
   */
  @Operation(summary = "알림 읽음 처리", 
             description = "특정 알림을 읽음 상태로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "403", description = "해당 알림에 대한 접근 권한 없음", content = @Content),
      @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
  })
  @PatchMapping("/{eventId}/read")
  ResponseEntity<Void> markAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "알림 이벤트 ID", example = "event-12345", required = true)
      @PathVariable String eventId
  );

  /**
   * 전체 알림 목록 읽음 처리
   */
  @Operation(summary = "전체 알림 읽음 처리", 
             description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 알림 읽음 처리 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @PatchMapping("/read-all")
  ResponseEntity<Void> markAllAsRead(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user
  );

  /**
   * 주어진 eventId에 해당하는 알림 삭제
   */
  @Operation(summary = "알림 삭제", 
             description = "특정 알림을 영구적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "알림 삭제 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
      @ApiResponse(responseCode = "403", description = "해당 알림에 대한 접근 권한 없음", content = @Content),
      @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
  })
  @DeleteMapping("/{eventId}")
  ResponseEntity<Void> deleteNotification(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user,
      @Parameter(description = "알림 이벤트 ID", example = "event-12345", required = true)
      @PathVariable String eventId
  );

  /**
   * 전체 알림 목록 삭제
   */
  @Operation(summary = "전체 알림 삭제", 
             description = "사용자의 모든 알림을 영구적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "전체 알림 삭제 성공", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
  })
  @DeleteMapping
  ResponseEntity<Void> deleteAllNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal JwtAuthenticationUser user
  );


}
