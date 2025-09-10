package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Notification 목록 조회 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetNotificationsByCursorApiTest extends BaseNotificationApiTest {

  private static final String VALID_USER_ID = "user123";
  private static final String OTHER_USER_ID = "user456";
  private static final String USER_TYPE = "USER";
  private static final String USER_STATUS_TYPE = "ACTIVE";

  @AfterEach
  void tearDown() {
    notificationRepository.deleteAll();
  }

  @Order(1)
  @Test
  @DisplayName("1. 기본 알림 목록 조회 (파라미터 없음) - 성공")
  void getNotifications_WithoutParams_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 테스트 알림들 생성
    createAndSaveMultipleNotifications(VALID_USER_ID, 5);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();

    assert data != null;
    assert data.content().size() <= 10; // 기본 limit
    assert data.content().size() > 0;
  }

  @Order(2)
  @Test
  @DisplayName("2. limit 파라미터로 알림 목록 조회 - 성공")
  void getNotifications_WithLimit_ShouldReturnLimitedResults() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    createAndSaveMultipleNotifications(VALID_USER_ID, 10);
    int requestLimit = 3;

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, requestLimit, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() <= requestLimit;
  }

  @Order(3)
  @Test
  @DisplayName("3. 커서 기반 페이지네이션 조회 - 성공")
  void getNotifications_WithCursor_ShouldReturnNextPage() throws Exception {
    // given
    int limit = 10;
    int size = 30;
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    createAndSaveMultipleNotifications(VALID_USER_ID, size);

    // 첫 번째 페이지 조회
    CommonResponse<CursorPageApiResponse<NotificationDetails>> firstPage =
        getNotificationsByCursorRequest(accessToken, null, null, limit, status().isOk());

    assert firstPage.isSuccess();
    CursorPageApiResponse<NotificationDetails> firstData = firstPage.getData();
    assert firstData.nextCursor() != null;
    assert firstData.nextCursor().cursorTimestamp() != null; // 다음 페이지 존재

    // 커서 정보 추출
    LocalDateTime cursorTimestamp = firstData.nextCursor().cursorTimestamp();
    String cursorId = firstData.nextCursor().cursorId();

    // when - 두 번째 페이지 조회
    CommonResponse<CursorPageApiResponse<NotificationDetails>> secondPage =
        getNotificationsByCursorRequest(accessToken, cursorTimestamp, cursorId, limit, status().isOk());

    // then
    assert secondPage.isSuccess();
    CursorPageApiResponse<NotificationDetails> secondData = secondPage.getData();
    assert secondData.content().size() > 0;

    // 첫 번째 페이지와 두 번째 페이지의 데이터가 다름을 확인
    List<String> firstPageEventIds = firstData.content().stream()
        .map(NotificationDetails::eventId)
        .toList();
    List<String> secondPageEventIds = secondData.content().stream()
        .map(NotificationDetails::eventId)
        .toList();

    assert firstPageEventIds.stream().noneMatch(secondPageEventIds::contains);
  }

  @Order(4)
  @Test
  @DisplayName("4. 다른 사용자의 알림은 조회되지 않음 - 성공")
  void getNotifications_ShouldNotReturnOtherUsersNotifications() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 현재 사용자의 알림
    createAndSaveMultipleNotifications(VALID_USER_ID, 3);
    // 다른 사용자의 알림
    createAndSaveMultipleNotifications(OTHER_USER_ID, 5);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, 20, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();

    // 모든 알림이 현재 사용자의 것인지 확인
    assert data.content().stream()
        .allMatch(notification -> VALID_USER_ID.equals(notification.receiverId()));
    assert data.content().size() == 3; // 현재 사용자의 알림만
  }

  @Order(5)
  @Test
  @DisplayName("5. 빈 결과 조회 - 성공")
  void getNotifications_WithNoNotifications_ShouldReturnEmptyList() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().isEmpty();
    assert data.nextCursor().cursorTimestamp() == null; // 다음 페이지 없음
  }

  @Order(6)
  @Test
  @DisplayName("6. 인증 없이 알림 목록 조회 - 400 Bad Request")
  void getNotifications_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(emptyToken, null, null, 10, status().isBadRequest());

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(7)
  @Test
  @DisplayName("7. 만료된 토큰으로 알림 목록 조회 - 401 Unauthorized")
  void getNotifications_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(expiredToken, null, null, 10, status().isUnauthorized());

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
  }

  @Order(8)
  @Test
  @DisplayName("8. 큰 limit 값으로 조회 - 성공")
  void getNotifications_WithLargeLimit_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    createAndSaveMultipleNotifications(VALID_USER_ID, 5);
    int largeLimit = 100;

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, largeLimit, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() == 5; // 실제 데이터 개수만큼만 반환
    assert data.nextCursor().cursorTimestamp() == null; // 더 이상 데이터 없음
  }

  @Order(9)
  @Test
  @DisplayName("9. ActorProfile 정보가 포함된 알림 목록 조회 - 성공")
  void getNotifications_WithActorProfile_ShouldReturnCorrectProfile() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    String customNickname = "테스트닉네임";
    String customProfileUrl = "https://test.com/custom.jpg";

    NotificationDoc notification = createTestNotification(VALID_USER_ID, "test-event",
        "test-post", "test-actor-user", customNickname, customProfileUrl);
    notificationRepository.save(notification);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() > 0;

    NotificationDetails firstNotification = data.content().get(0);
    ActorProfile actorProfile = firstNotification.actorProfile();
    assert actorProfile != null;
    assert actorProfile.nickname().equals(customNickname);
    assert actorProfile.profileImageUrl().equals(customProfileUrl);
  }

  @Order(10)
  @Test
  @DisplayName("10. 시간순 정렬 검증 - 성공")
  void getNotifications_ShouldReturnInTimeOrder() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 시간차를 두고 알림 생성
    NotificationDoc oldNotification = createTestNotification(VALID_USER_ID, "old-event",
        "old-post", "old-liker");
    notificationRepository.save(oldNotification);

    Thread.sleep(100); // 시간 차이 확보

    NotificationDoc newNotification = createTestNotification(VALID_USER_ID, "new-event",
        "new-post", "new-liker");
    notificationRepository.save(newNotification);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() >= 2;

    // 최신순 정렬 확인 (첫 번째가 더 최근이어야 함)
    LocalDateTime firstTimestamp = data.content().get(0).occurredAt();
    LocalDateTime secondTimestamp = data.content().get(1).occurredAt();
    assert firstTimestamp.isAfter(secondTimestamp) || firstTimestamp.isEqual(secondTimestamp);
  }

  /**
   * 여러 개의 테스트 알림 생성 및 저장
   */
  private void createAndSaveMultipleNotifications(String receiverId, int count) {
    for (int i = 0; i < count; i++) {
      NotificationDoc notification = createTestNotification(receiverId,
          "event-" + Math.random(), "post-" + i, "liker-" + i, "nickname-" + i,
          "https://test.com/profile" + i + ".jpg");
      notificationRepository.save(notification);

      // 시간 차이를 위한 작은 지연
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}