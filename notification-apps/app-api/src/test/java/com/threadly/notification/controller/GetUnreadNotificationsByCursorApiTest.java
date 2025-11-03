package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("읽지 않은 알림 목록 조회 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetUnreadNotificationsByCursorApiTest extends BaseNotificationApiTest {

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
  @DisplayName("1. 기본 읽지 않은 알림 목록 조회 (파라미터 없음) - 성공")
  void getUnreadNotifications_WithoutParams_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 읽지 않은 알림과 읽은 알림을 각각 생성
    createAndSaveUnreadNotifications(VALID_USER_ID, 3);
    createAndSaveReadNotifications(VALID_USER_ID, 2);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();

    assert data != null;
    assert data.content().size() == 3; // 읽지 않은 알림만
    assert data.content().stream().noneMatch(NotificationDetails::isRead); // 모든 알림이 읽지 않음
  }

  @Order(2)
  @Test
  @DisplayName("2. limit 파라미터로 읽지 않은 알림 목록 조회 - 성공")
  void getUnreadNotifications_WithLimit_ShouldReturnLimitedResults() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    createAndSaveUnreadNotifications(VALID_USER_ID, 10);
    createAndSaveReadNotifications(VALID_USER_ID, 5);
    int requestLimit = 3;

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, requestLimit, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() <= requestLimit;
    assert data.content().stream().noneMatch(NotificationDetails::isRead); // 모든 결과가 읽지 않음
  }

  @Order(3)
  @Test
  @DisplayName("3. 커서 기반 페이지네이션으로 읽지 않은 알림 조회 - 성공")
  void getUnreadNotifications_WithCursor_ShouldReturnNextPage() throws Exception {
    // given
    int limit = 5;
    int unreadCount = 15;
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    createAndSaveUnreadNotifications(VALID_USER_ID, unreadCount);
    createAndSaveReadNotifications(VALID_USER_ID, 10); // 읽은 알림도 생성

    // 첫 번째 페이지 조회
    CommonResponse<CursorPageApiResponse<NotificationDetails>> firstPage =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, limit, status().isOk());

    assert firstPage.isSuccess();
    CursorPageApiResponse<NotificationDetails> firstData = firstPage.getData();
    assert firstData.nextCursor() != null;
    assert firstData.nextCursor().cursorTimestamp() != null; // 다음 페이지 존재
    assert firstData.content().stream().noneMatch(NotificationDetails::isRead);

    // 커서 정보 추출
    LocalDateTime cursorTimestamp = firstData.nextCursor().cursorTimestamp();
    String cursorId = firstData.nextCursor().cursorId();

    // when - 두 번째 페이지 조회
    CommonResponse<CursorPageApiResponse<NotificationDetails>> secondPage =
        getUnreadNotificationsByCursorRequest(accessToken, cursorTimestamp, cursorId, limit, status().isOk());

    // then
    assert secondPage.isSuccess();
    CursorPageApiResponse<NotificationDetails> secondData = secondPage.getData();
    assert secondData.content().size() > 0;
    assert secondData.content().stream().noneMatch(NotificationDetails::isRead);

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
  @DisplayName("4. 다른 사용자의 읽지 않은 알림은 조회되지 않음 - 성공")
  void getUnreadNotifications_ShouldNotReturnOtherUsersNotifications() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 현재 사용자의 읽지 않은 알림
    createAndSaveUnreadNotifications(VALID_USER_ID, 3);
    // 다른 사용자의 읽지 않은 알림
    createAndSaveUnreadNotifications(OTHER_USER_ID, 5);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 20, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();

    // 모든 알림이 현재 사용자의 것인지 확인
    assert data.content().stream()
        .allMatch(notification -> VALID_USER_ID.equals(notification.receiverId()));
    assert data.content().size() == 3; // 현재 사용자의 읽지 않은 알림만
    assert data.content().stream().noneMatch(NotificationDetails::isRead);
  }

  @Order(5)
  @Test
  @DisplayName("5. 읽지 않은 알림이 없는 경우 - 빈 결과 반환")
  void getUnreadNotifications_WithNoUnreadNotifications_ShouldReturnEmptyList() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 읽은 알림만 생성
    createAndSaveReadNotifications(VALID_USER_ID, 5);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().isEmpty();
    assert data.nextCursor().cursorTimestamp() == null; // 다음 페이지 없음
  }

  @Order(6)
  @Test
  @DisplayName("6. 알림이 전혀 없는 경우 - 빈 결과 반환")
  void getUnreadNotifications_WithNoNotifications_ShouldReturnEmptyList() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().isEmpty();
    assert data.nextCursor().cursorTimestamp() == null; // 다음 페이지 없음
  }

  @Order(7)
  @Test
  @DisplayName("7. 인증 없이 읽지 않은 알림 목록 조회 - 400 Bad Request")
  void getUnreadNotifications_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(emptyToken, null, null, 10, status().isBadRequest());

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(8)
  @Test
  @DisplayName("8. 만료된 토큰으로 읽지 않은 알림 목록 조회 - 401 Unauthorized")
  void getUnreadNotifications_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(expiredToken, null, null, 10, status().isUnauthorized());

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
  }

  @Order(9)
  @Test
  @DisplayName("9. 읽음 상태 혼재 시 읽지 않은 알림만 조회 - 성공")
  void getUnreadNotifications_WithMixedReadStatus_ShouldReturnOnlyUnread() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 읽지 않은 알림 3개, 읽은 알림 7개 생성
    createAndSaveUnreadNotifications(VALID_USER_ID, 3);
    createAndSaveReadNotifications(VALID_USER_ID, 7);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 20, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() == 3; // 읽지 않은 알림만
    assert data.content().stream().noneMatch(NotificationDetails::isRead); // 모든 결과가 읽지 않음
  }

  @Order(10)
  @Test
  @DisplayName("10. 시간순 정렬 검증 - 읽지 않은 알림만 최신순으로 정렬")
  void getUnreadNotifications_ShouldReturnInTimeOrder() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 시간차를 두고 읽지 않은 알림 생성
    NotificationDoc oldUnreadNotification = createTestNotification(VALID_USER_ID, "old-event",
        "old-post", "old-liker", "Old User", "https://old.com/profile.jpg", false);
    notificationRepository.save(oldUnreadNotification);

    Thread.sleep(100); // 시간 차이 확보

    NotificationDoc newUnreadNotification = createTestNotification(VALID_USER_ID, "new-event",
        "new-post", "new-liker", "New User", "https://new.com/profile.jpg", false);
    notificationRepository.save(newUnreadNotification);

    // 읽은 알림도 추가 (조회에서 제외되어야 함)
    Thread.sleep(100);
    NotificationDoc readNotification = createTestNotification(VALID_USER_ID, "read-event",
        "read-post", "read-liker", "Read User", "https://read.com/profile.jpg", true);
    notificationRepository.save(readNotification);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() == 2; // 읽지 않은 알림만

    // 모든 결과가 읽지 않음 확인
    assert data.content().stream().noneMatch(NotificationDetails::isRead);

    // 최신순 정렬 확인 (첫 번째가 더 최근이어야 함)
    LocalDateTime firstTimestamp = data.content().get(0).occurredAt();
    LocalDateTime secondTimestamp = data.content().get(1).occurredAt();
    assert firstTimestamp.isAfter(secondTimestamp) || firstTimestamp.isEqual(secondTimestamp);

    // 알림 ID로 정확성 검증
    List<String> eventIds = data.content().stream()
        .map(NotificationDetails::eventId)
        .toList();
    assert eventIds.contains("new-event");
    assert eventIds.contains("old-event");
    assert !eventIds.contains("read-event"); // 읽은 알림은 포함되지 않아야 함
  }

  @Order(11)
  @Test
  @DisplayName("11. ActorProfile 정보가 포함된 읽지 않은 알림 조회 - 성공")
  void getUnreadNotifications_WithActorProfile_ShouldReturnCorrectProfile() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    String customNickname = "읽지않은알림테스터";
    String customProfileUrl = "https://unread.com/custom.jpg";

    NotificationDoc unreadNotification = createTestNotification(VALID_USER_ID, "unread-event",
        "unread-post", "unread-actor-user", customNickname, customProfileUrl, false);
    notificationRepository.save(unreadNotification);

    // 읽은 알림도 추가 (조회에서 제외되어야 함)
    NotificationDoc readNotification = createTestNotification(VALID_USER_ID, "read-event",
        "read-post", "read-actor-user", "ReadUser", "https://read.com/profile.jpg", true);
    notificationRepository.save(readNotification);

    // when
    CommonResponse<CursorPageApiResponse<NotificationDetails>> response =
        getUnreadNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());

    // then
    assert response.isSuccess();
    CursorPageApiResponse<NotificationDetails> data = response.getData();
    assert data.content().size() == 1; // 읽지 않은 알림만

    NotificationDetails unreadNotificationDetail = data.content().get(0);
    assert !unreadNotificationDetail.isRead(); // 읽지 않음 확인
    
    ActorProfile actorProfile = unreadNotificationDetail.actorProfile();
    assert actorProfile != null;
    assert actorProfile.getNickname().equals(customNickname);
    assert actorProfile.getProfileImageUrl().equals(customProfileUrl);
  }

  /**
   * 읽지 않은 알림들 생성 및 저장
   */
  private void createAndSaveUnreadNotifications(String receiverId, int count) {
    for (int i = 0; i < count; i++) {
      NotificationDoc notification = createTestNotification(receiverId,
          "unread-event-" + Math.random(), "unread-post-" + i, "unread-liker-" + i, 
          "UnreadUser-" + i, "https://unread.com/profile" + i + ".jpg", false);
      notificationRepository.save(notification);

      // 시간 차이를 위한 작은 지연
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * 읽은 알림들 생성 및 저장
   */
  private void createAndSaveReadNotifications(String receiverId, int count) {
    for (int i = 0; i < count; i++) {
      NotificationDoc notification = createTestNotification(receiverId,
          "read-event-" + Math.random(), "read-post-" + i, "read-liker-" + i, 
          "ReadUser-" + i, "https://read.com/profile" + i + ".jpg", true);
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