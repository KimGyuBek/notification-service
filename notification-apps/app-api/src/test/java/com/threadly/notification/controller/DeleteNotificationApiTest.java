package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.commons.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Notification 삭제 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteNotificationApiTest extends BaseNotificationApiTest {

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
  @DisplayName("1. 존재하는 알림 삭제 - 성공")
  void deleteNotification_ExistingNotification_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // 삭제 전 존재 확인
    assert notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(),
        VALID_USER_ID);

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();

    // 삭제 후 존재하지 않음 확인
    assert !notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(),
        VALID_USER_ID);
  }

  @Order(2)
  @Test
  @DisplayName("2. 존재하지 않는 알림 삭제 - 404 Not Found")
  void deleteNotification_NonExistentNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String nonExistentEventId = "non-existent-event-id";

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, nonExistentEventId, status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(3)
  @Test
  @DisplayName("3. 다른 사용자의 알림 삭제 - 404 NotFound")
  void deleteNotification_OtherUserNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity otherUserNotification = createTestNotification(OTHER_USER_ID);
    notificationRepository.save(otherUserNotification);

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, otherUserNotification.getEventId(), status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);

    // 다른 사용자의 알림은 삭제되지 않음 확인
    assert notificationRepository.existsByEventIdAndReceiverId(
        otherUserNotification.getEventId(), OTHER_USER_ID);
  }

  @Order(4)
  @Test
  @DisplayName("4. 인증 없이 알림 삭제 - 400 Bad Request")
  void deleteNotification_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        emptyToken, notification.getEventId(), status().isBadRequest()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);

    // 알림은 삭제되지 않음 확인
    assert notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(),
        VALID_USER_ID);
  }

  @Order(5)
  @Test
  @DisplayName("5. 만료된 토큰으로 알림 삭제 - 401 Unauthorized")
  void deleteNotification_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        expiredToken, notification.getEventId(), status().isUnauthorized()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);

    // 알림은 삭제되지 않음 확인
    assert notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(),
        VALID_USER_ID);
  }

  @Order(6)
  @Test
  @DisplayName("6. 특수 문자가 포함된 eventId로 알림 삭제 - 404 Not Found")
  void deleteNotification_WithSpecialCharacterEventId_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String specialEventId = "event-id-with-special-chars!@#$%";

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, specialEventId, status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(7)
  @Test
  @DisplayName("7. 여러 알림 중 특정 알림만 삭제 - 성공")
  void deleteNotification_MultipleNotificationsExist_ShouldDeleteOnlyTargetOne() throws Exception {
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

    // when - 두 번째 알림 삭제
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, notification2.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();

    // 삭제된 알림 확인
    assert !notificationRepository.existsByEventIdAndReceiverId("event2", VALID_USER_ID);

    // 다른 알림들은 여전히 존재 확인
    assert notificationRepository.existsByEventIdAndReceiverId("event1", VALID_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("event3", VALID_USER_ID);
  }

  @Order(8)
  @Test
  @DisplayName("8. 이미 삭제된 알림 재삭제 시도 - 404 Not Found")
  void deleteNotification_AlreadyDeletedNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // 첫 번째 삭제
    deleteNotificationRequest(accessToken, notification.getEventId(), status().isOk());

    // 삭제 확인
    assert !notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(),
        VALID_USER_ID);

    // when - 이미 삭제된 알림 재삭제 시도
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, notification.getEventId(), status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(9)
  @Test
  @DisplayName("9. 권한이 있는 사용자의 알림 삭제 후 데이터 완전 제거 검증")
  void deleteNotification_AuthorizedUser_ShouldCompletelyRemoveData() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String testEventId = "test-event-for-complete-removal";
    NotificationEntity notification = createTestNotification(VALID_USER_ID, testEventId,
        "test-post", "test-actor-user", "test-nickname", "https://test.com/profile.jpg");
    notificationRepository.save(notification);

    // 삭제 전 데이터 존재 확인
    Optional<NotificationEntity> beforeDelete = notificationRepository.findById(testEventId);
    assert beforeDelete.isPresent();

    // when
    CommonResponse<Void> response = deleteNotificationRequest(
        accessToken, testEventId, status().isOk()
    );

    // then
    assert response.isSuccess();

    // 완전한 데이터 제거 확인
    Optional<NotificationEntity> afterDelete = notificationRepository.findById(testEventId);
    assert afterDelete.isEmpty();
    assert !notificationRepository.existsByEventIdAndReceiverId(testEventId, VALID_USER_ID);
  }
}