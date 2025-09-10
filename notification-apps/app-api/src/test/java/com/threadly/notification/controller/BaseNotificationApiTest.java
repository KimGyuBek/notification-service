package com.threadly.notification.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.notification.BaseApiTest;
import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.adapter.persistence.notification.repository.MongoNotificationRepository;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.utils.AccessTokenTestUtils;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

public class BaseNotificationApiTest extends BaseApiTest {

  @Autowired
  public AccessTokenTestUtils accessTokenTestUtils;

  @Autowired
  public MongoNotificationRepository notificationRepository;

  /**
   * 알림 상세 정보 조회 요청
   *
   * @return
   */
  public CommonResponse<GetNotificationDetailsApiResponse> getNotificationDetailsRequest(
      String accessToken, String eventId,
      ResultMatcher expectedStatus)
      throws Exception {
    return sendGetRequest(
        accessToken, "/api/notifications/" + eventId,
        expectedStatus,
        new TypeReference<>() {
        }
    );
  }

  /**
   * 알림 목록 커서 기반 조회 요청
   *
   * @return
   */
  public CommonResponse<CursorPageApiResponse<NotificationDetails>> getNotificationsByCursorRequest(
      String accessToken,
      LocalDateTime cursorTimestamp,
      String cursorId,
      int limit,
      ResultMatcher expectedStatus)
      throws Exception {

    StringBuilder urlBuilder = new StringBuilder("/api/notifications");
    String separator = "?";

    if (cursorTimestamp != null) {
      urlBuilder.append(separator).append("cursor_timestamp=").append(cursorTimestamp);
      separator = "&";
    }

    if (cursorId != null) {
      urlBuilder.append(separator).append("cursor_id=").append(cursorId);
      separator = "&";
    }

    if (limit > 0) {
      urlBuilder.append(separator).append("limit=").append(limit);
    }

    return sendGetRequest(
        accessToken,
        urlBuilder.toString(),
        expectedStatus,
        new TypeReference<>() {
        }
    );
  }

  /**
   * 알림 삭제 요청
   *
   * @return
   */
  public CommonResponse<Void> deleteNotificationRequest(
      String accessToken,
      String eventId,
      ResultMatcher expectedStatus)
      throws Exception {
    return sendDeleteRequest(
        accessToken,
        "/api/notifications/" + eventId,
        expectedStatus,
        new TypeReference<>() {
        },
        Map.of("Authorization", "Bearer " + accessToken)
    );
  }

  /**
   * 전체 알림 삭제 요청
   *
   * @return
   */
  public CommonResponse<Void> deleteAllNotificationsRequest(
      String accessToken,
      ResultMatcher expectedStatus)
      throws Exception {
    return sendDeleteRequest(
        accessToken,
        "/api/notifications",
        expectedStatus,
        new TypeReference<>() {
        },
        Map.of("Authorization", "Bearer " + accessToken)
    );
  }

  /**
   * 알림 읽음 처리 요청
   *
   * @return
   */
  public CommonResponse<Void> markAsReadNotificationRequest(
      String accessToken,
      String eventId,
      ResultMatcher expectedStatus)
      throws Exception {
    return sendPatchRequest(
        accessToken,
        "/api/notifications/" + eventId + "/read",
        expectedStatus,
        new TypeReference<>() { },
        Map.of("Authorization", "Bearer " + accessToken)
    );
  }

  /**
   * 전체 알림 읽음 처리 요청
   *
   * @return
   */
  public CommonResponse<Void> markAllAsReadNotificationsRequest(
      String accessToken,
      ResultMatcher expectedStatus)
      throws Exception {
    return sendPatchRequest(
        accessToken,
        "/api/notifications/read-all",
        expectedStatus,
        new TypeReference<>() { },
        Map.of("Authorization", "Bearer " + accessToken)
    );
  }

  /**
   * 테스트용 알림 엔티티 생성 (기본값)
   */
  public NotificationDoc createTestNotification(String receiverId) {
    return createTestNotification(receiverId, UUID.randomUUID().toString(), "test-post",
        "test-actor-user", "test-nickname", "https://test.com/profile.jpg");
  }

  /**
   * 테스트용 알림 엔티티 생성 (상세 설정)
   */
  public NotificationDoc createTestNotification(String receiverId, String eventId, String postId,
      String actorUserId) {
    return createTestNotification(receiverId, eventId, postId, actorUserId, "test-nickname",
        "https://test.com/profile.jpg");
  }

  /**
   * 테스트용 알림 엔티티 생성 (전체 설정)
   */
  public NotificationDoc createTestNotification(String receiverId, String eventId, String postId,
      String actorUserId, String nickname, String profileImageUrl) {
    PostLikeMeta metadata = new PostLikeMeta(postId);
    ActorProfile actorProfile = new ActorProfile(actorUserId, nickname, profileImageUrl);

    return new NotificationDoc(
        eventId,
        receiverId,
        NotificationType.POST_LIKE,
        metadata,
        LocalDateTime.now(),
        actorProfile
    );
  }

}
