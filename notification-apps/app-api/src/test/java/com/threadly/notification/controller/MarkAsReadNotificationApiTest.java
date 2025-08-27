package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("단건 알림 읽음 처리 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarkAsReadNotificationApiTest extends BaseNotificationApiTest {

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
  @DisplayName("1. 읽지 않은 알림 읽음 처리 - 성공")
  void markAsReadNotification_UnreadNotification_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // 읽기 전 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> beforeRead = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );
    assert !beforeRead.getData().isRead(); // 읽지 않은 상태

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();

    // 읽음 처리 후 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> afterRead = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );
    assert afterRead.getData().isRead(); // 읽음 상태로 변경됨
  }

  @Order(2)
  @Test
  @DisplayName("2. 이미 읽은 알림 읽음 처리 - 성공 (Idempotent)")
  void markAsReadNotification_AlreadyReadNotification_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // 첫 번째 읽음 처리
    markAsReadNotificationRequest(accessToken, notification.getEventId(), status().isOk());

    // 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> firstRead = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );
    assert firstRead.getData().isRead();

    // when - 이미 읽은 알림에 대해 다시 읽음 처리
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess(); // Idempotent 동작 확인

    // 여전히 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> secondRead = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );
    assert secondRead.getData().isRead();
  }

  @Order(3)
  @Test
  @DisplayName("3. 존재하지 않는 알림 읽음 처리 - 404 Not Found")
  void markAsReadNotification_NonExistentNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String nonExistentEventId = "non-existent-event-id";

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, nonExistentEventId, status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(4)
  @Test
  @DisplayName("4. 다른 사용자의 알림 읽음 처리 - 404 NotFound")
  void markAsReadNotification_OtherUserNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity otherUserNotification = createTestNotification(OTHER_USER_ID);
    notificationRepository.save(otherUserNotification);

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, otherUserNotification.getEventId(), status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(5)
  @Test
  @DisplayName("5. 인증 없이 알림 읽음 처리 - 400 Bad Request")
  void markAsReadNotification_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        emptyToken, notification.getEventId(), status().isBadRequest()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(6)
  @Test
  @DisplayName("6. 만료된 토큰으로 알림 읽음 처리 - 401 Unauthorized")
  void markAsReadNotification_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        expiredToken, notification.getEventId(), status().isUnauthorized()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
  }

  @Order(7)
  @Test
  @DisplayName("7. 여러 알림 중 특정 알림만 읽음 처리 - 성공")
  void markAsReadNotification_MultipleNotificationsExist_ShouldMarkOnlyTargetOne()
      throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // 같은 사용자의 여러 알림 생성
    NotificationEntity notification1 = createTestNotification(VALID_USER_ID, "event1", "post1",
        "liker1");
    NotificationEntity notification2 = createTestNotification(VALID_USER_ID, "event2", "post2",
        "liker2");
    NotificationEntity notification3 = createTestNotification(VALID_USER_ID, "event3", "post3",
        "liker3");

    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
    notificationRepository.save(notification3);

    // when - 두 번째 알림만 읽음 처리
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, "event2", status().isOk()
    );

    // then
    assert response.isSuccess();

    // 두 번째 알림만 읽음 상태로 변경되었는지 확인
    CommonResponse<GetNotificationDetailsApiResponse> notification1Data = getNotificationDetailsRequest(
        accessToken, "event1", status().isOk()
    );
    CommonResponse<GetNotificationDetailsApiResponse> notification2Data = getNotificationDetailsRequest(
        accessToken, "event2", status().isOk()
    );
    CommonResponse<GetNotificationDetailsApiResponse> notification3Data = getNotificationDetailsRequest(
        accessToken, "event3", status().isOk()
    );

    assert !notification1Data.getData().isRead(); // 읽지 않음
    assert notification2Data.getData().isRead();  // 읽음
    assert !notification3Data.getData().isRead(); // 읽지 않음
  }

  @Order(8)
  @Test
  @DisplayName("8. 읽음 처리 후 데이터 영속성 검증")
  void markAsReadNotification_AfterMarkingAsRead_ShouldPersistReadStatus() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String testEventId = "persistent-test-event";
    NotificationEntity notification = createTestNotification(VALID_USER_ID, testEventId,
        "test-post", "test-actor-user", "test-nickname", "https://test.com/profile.jpg");
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = markAsReadNotificationRequest(
        accessToken, testEventId, status().isOk()
    );

    // then
    assert response.isSuccess();

    // DB에서 직접 조회하여 읽음 상태 확인
    Optional<NotificationEntity> updatedNotification = notificationRepository.findByEventIdAndReceiverId(
        testEventId, VALID_USER_ID);
    assert updatedNotification.isPresent();
    assert updatedNotification.get().isRead(); // DB에 영속화된 읽음 상태 확인

    // API 조회로도 확인
    CommonResponse<GetNotificationDetailsApiResponse> apiResponse = getNotificationDetailsRequest(
        accessToken, testEventId, status().isOk()
    );
    assert apiResponse.getData().isRead();
  }
}