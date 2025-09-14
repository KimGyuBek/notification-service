package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.commons.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("전체 알림 삭제 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllNotificationsApiTest extends BaseNotificationApiTest {

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
  @DisplayName("1. 여러 알림 전체 삭제 - 성공")
  void deleteAllNotifications_WithMultipleNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 현재 사용자의 여러 알림 생성
    NotificationDoc notification1 = createTestNotification(VALID_USER_ID, "event1", "post1", "liker1");
    NotificationDoc notification2 = createTestNotification(VALID_USER_ID, "event2", "post2", "liker2");
    NotificationDoc notification3 = createTestNotification(VALID_USER_ID, "event3", "post3", "liker3");
    
    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
    notificationRepository.save(notification3);
    
    // 삭제 전 존재 확인
    assert notificationRepository.existsByEventIdAndReceiverId("event1", VALID_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("event2", VALID_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("event3", VALID_USER_ID);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 모든 알림이 삭제되었는지 확인
    assert !notificationRepository.existsByEventIdAndReceiverId("event1", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("event2", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("event3", VALID_USER_ID);
  }

  @Order(2)
  @Test
  @DisplayName("2. 알림이 없는 상태에서 전체 삭제 - 성공 (Idempotent)")
  void deleteAllNotifications_WithNoNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when - 알림이 없는 상태에서 전체 삭제 요청
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess(); // Idempotent 동작 확인
  }

  @Order(3)
  @Test
  @DisplayName("3. 다른 사용자의 알림은 삭제되지 않음 - 성공")
  void deleteAllNotifications_ShouldNotDeleteOtherUsersNotifications() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 현재 사용자의 알림
    NotificationDoc myNotification1 = createTestNotification(VALID_USER_ID, "my-event1", "my-post1", "liker1");
    NotificationDoc myNotification2 = createTestNotification(VALID_USER_ID, "my-event2", "my-post2", "liker2");
    
    // 다른 사용자의 알림
    NotificationDoc otherNotification1 = createTestNotification(OTHER_USER_ID, "other-event1", "other-post1", "other-liker1");
    NotificationDoc otherNotification2 = createTestNotification(OTHER_USER_ID, "other-event2", "other-post2", "other-liker2");
    
    notificationRepository.save(myNotification1);
    notificationRepository.save(myNotification2);
    notificationRepository.save(otherNotification1);
    notificationRepository.save(otherNotification2);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 현재 사용자의 알림만 삭제되었는지 확인
    assert !notificationRepository.existsByEventIdAndReceiverId("my-event1", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("my-event2", VALID_USER_ID);
    
    // 다른 사용자의 알림은 남아있는지 확인
    assert notificationRepository.existsByEventIdAndReceiverId("other-event1", OTHER_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("other-event2", OTHER_USER_ID);
  }

  @Order(4)
  @Test
  @DisplayName("4. 인증 없이 전체 알림 삭제 - 400 Bad Request")
  void deleteAllNotifications_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";
    
    // 테스트 알림 생성
    NotificationDoc notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        emptyToken, status().isBadRequest()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
    
    // 알림은 삭제되지 않음 확인
    assert notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(), VALID_USER_ID);
  }

  @Order(5)
  @Test
  @DisplayName("5. 만료된 토큰으로 전체 알림 삭제 - 401 Unauthorized")
  void deleteAllNotifications_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 테스트 알림 생성
    NotificationDoc notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        expiredToken, status().isUnauthorized()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
    
    // 알림은 삭제되지 않음 확인
    assert notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(), VALID_USER_ID);
  }

  @Order(6)
  @Test
  @DisplayName("6. 부분적으로 알림이 있는 상태에서 전체 삭제 - 성공")
  void deleteAllNotifications_WithPartialNotifications_ShouldDeleteAll() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 현재 사용자의 알림 중 일부만 생성
    NotificationDoc notification1 = createTestNotification(VALID_USER_ID, "partial-event1", "partial-post1", "partial-liker1");
    notificationRepository.save(notification1);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    assert !notificationRepository.existsByEventIdAndReceiverId("partial-event1", VALID_USER_ID);
  }

  @Order(7)
  @Test
  @DisplayName("7. 전체 삭제 후 다시 전체 삭제 - 성공 (Idempotent)")
  void deleteAllNotifications_AfterAlreadyDeleted_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    NotificationDoc notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);
    
    // 첫 번째 전체 삭제
    CommonResponse<Void> firstResponse = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );
    assert firstResponse.isSuccess();
    assert !notificationRepository.existsByEventIdAndReceiverId(notification.getEventId(), VALID_USER_ID);

    // when - 이미 삭제된 상태에서 다시 전체 삭제 요청
    CommonResponse<Void> secondResponse = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert secondResponse.isSuccess(); // Idempotent 동작 확인
  }

  @Order(8)
  @Test
  @DisplayName("8. 대량의 알림 전체 삭제 - 성공")
  void deleteAllNotifications_WithLargeAmountOfNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 대량 알림 생성 (50개)
    for (int i = 0; i < 50; i++) {
      NotificationDoc notification = createTestNotification(VALID_USER_ID, "bulk-event-" + i,
          "bulk-post-" + i, "bulk-liker-" + i, "bulk-nickname-" + i, 
          "https://test.com/bulk-profile" + i + ".jpg");
      notificationRepository.save(notification);
    }
    
    // 생성 확인 (샘플링)
    assert notificationRepository.existsByEventIdAndReceiverId("bulk-event-0", VALID_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("bulk-event-25", VALID_USER_ID);
    assert notificationRepository.existsByEventIdAndReceiverId("bulk-event-49", VALID_USER_ID);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 모든 대량 알림이 삭제되었는지 확인 (샘플링)
    assert !notificationRepository.existsByEventIdAndReceiverId("bulk-event-0", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("bulk-event-25", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("bulk-event-49", VALID_USER_ID);
  }

  @Order(9)
  @Test
  @DisplayName("9. 혼합된 알림 타입의 전체 삭제 - 성공")
  void deleteAllNotifications_WithMixedNotificationTypes_ShouldDeleteAll() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 다양한 설정의 알림들 생성
    NotificationDoc normalNotification = createTestNotification(VALID_USER_ID, "normal-event",
        "normal-post", "normal-liker", "normal-nickname", "https://normal.com/profile.jpg");
    
    NotificationDoc customNotification = createTestNotification(VALID_USER_ID, "custom-event",
        "custom-post", "custom-liker", "커스텀닉네임", "https://custom.com/특별한/프로필.jpg");
    
    notificationRepository.save(normalNotification);
    notificationRepository.save(customNotification);

    // when
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 모든 타입의 알림이 삭제되었는지 확인
    assert !notificationRepository.existsByEventIdAndReceiverId("normal-event", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("custom-event", VALID_USER_ID);
  }

  @Order(10)
  @Test
  @DisplayName("10. 전체 삭제 성능 및 안정성 검증")
  void deleteAllNotifications_PerformanceAndStabilityTest() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 다양한 패턴의 알림 생성
    for (int i = 0; i < 20; i++) {
      NotificationDoc notification = createTestNotification(VALID_USER_ID, "perf-event-" + i,
          "perf-post-" + i, "perf-liker-" + i);
      notificationRepository.save(notification);
    }
    
    long beforeCount = notificationRepository.count();
    assert beforeCount >= 20; // 최소 20개 이상 생성됨

    // when
    long startTime = System.currentTimeMillis();
    CommonResponse<Void> response = deleteAllNotificationsRequest(
        accessToken, status().isOk()
    );
    long endTime = System.currentTimeMillis();

    // then
    assert response.isSuccess();
    
    // 성능 검증 (1초 이내 완료)
    assert (endTime - startTime) < 1000;
    
    // 현재 사용자의 알림만 삭제되었는지 확인 (샘플링)
    assert !notificationRepository.existsByEventIdAndReceiverId("perf-event-0", VALID_USER_ID);
    assert !notificationRepository.existsByEventIdAndReceiverId("perf-event-19", VALID_USER_ID);
  }
}