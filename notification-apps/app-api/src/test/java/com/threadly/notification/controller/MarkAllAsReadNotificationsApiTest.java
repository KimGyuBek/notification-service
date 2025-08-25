package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.response.CursorPageApiResponse;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("전체 알림 읽음 처리 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarkAllAsReadNotificationsApiTest extends BaseNotificationApiTest {

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
  @DisplayName("1. 여러 읽지 않은 알림 전체 읽음 처리 - 성공")
  void markAllAsReadNotifications_WithUnreadNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 현재 사용자의 여러 알림 생성
    NotificationEntity notification1 = createTestNotification(VALID_USER_ID, "event1", "post1", "liker1");
    NotificationEntity notification2 = createTestNotification(VALID_USER_ID, "event2", "post2", "liker2");
    NotificationEntity notification3 = createTestNotification(VALID_USER_ID, "event3", "post3", "liker3");
    
    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
    notificationRepository.save(notification3);
    
    // 읽기 전 모든 알림이 읽지 않은 상태인지 확인
    CommonResponse<GetNotificationDetailsApiResponse> beforeRead1 = getNotificationDetailsRequest(
        accessToken, "event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> beforeRead2 = getNotificationDetailsRequest(
        accessToken, "event2", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> beforeRead3 = getNotificationDetailsRequest(
        accessToken, "event3", status().isOk());
    
    assert !beforeRead1.getData().isRead();
    assert !beforeRead2.getData().isRead();
    assert !beforeRead3.getData().isRead();

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 모든 알림이 읽음 상태로 변경되었는지 확인
    CommonResponse<GetNotificationDetailsApiResponse> afterRead1 = getNotificationDetailsRequest(
        accessToken, "event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> afterRead2 = getNotificationDetailsRequest(
        accessToken, "event2", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> afterRead3 = getNotificationDetailsRequest(
        accessToken, "event3", status().isOk());
    
    assert afterRead1.getData().isRead();
    assert afterRead2.getData().isRead();
    assert afterRead3.getData().isRead();
  }

  @Order(2)
  @Test
  @DisplayName("2. 알림이 없는 상태에서 전체 읽음 처리 - 성공 (Idempotent)")
  void markAllAsReadNotifications_WithNoNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when - 알림이 없는 상태에서 전체 읽음 처리 요청
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess(); // Idempotent 동작 확인
  }

  @Order(3)
  @Test
  @DisplayName("3. 이미 모든 알림이 읽음 상태에서 전체 읽음 처리 - 성공 (Idempotent)")
  void markAllAsReadNotifications_WithAllReadNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    NotificationEntity notification1 = createTestNotification(VALID_USER_ID, "event1", "post1", "liker1");
    NotificationEntity notification2 = createTestNotification(VALID_USER_ID, "event2", "post2", "liker2");
    
    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
    
    // 먼저 모든 알림을 읽음 처리
    markAllAsReadNotificationsRequest(accessToken, status().isOk());
    
    // 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> firstCheck1 = getNotificationDetailsRequest(
        accessToken, "event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> firstCheck2 = getNotificationDetailsRequest(
        accessToken, "event2", status().isOk());
    
    assert firstCheck1.getData().isRead();
    assert firstCheck2.getData().isRead();

    // when - 이미 읽음 상태에서 다시 전체 읽음 처리
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess(); // Idempotent 동작 확인
    
    // 여전히 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> secondCheck1 = getNotificationDetailsRequest(
        accessToken, "event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> secondCheck2 = getNotificationDetailsRequest(
        accessToken, "event2", status().isOk());
    
    assert secondCheck1.getData().isRead();
    assert secondCheck2.getData().isRead();
  }

  @Order(4)
  @Test
  @DisplayName("4. 다른 사용자의 알림은 읽음 처리되지 않음 - 성공")
  void markAllAsReadNotifications_ShouldNotMarkOtherUsersNotifications() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 현재 사용자의 알림
    NotificationEntity myNotification1 = createTestNotification(VALID_USER_ID, "my-event1", "my-post1", "liker1");
    NotificationEntity myNotification2 = createTestNotification(VALID_USER_ID, "my-event2", "my-post2", "liker2");
    
    // 다른 사용자의 알림
    NotificationEntity otherNotification1 = createTestNotification(OTHER_USER_ID, "other-event1", "other-post1", "other-liker1");
    NotificationEntity otherNotification2 = createTestNotification(OTHER_USER_ID, "other-event2", "other-post2", "other-liker2");
    
    notificationRepository.save(myNotification1);
    notificationRepository.save(myNotification2);
    notificationRepository.save(otherNotification1);
    notificationRepository.save(otherNotification2);

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 현재 사용자의 알림만 읽음 처리되었는지 확인
    CommonResponse<GetNotificationDetailsApiResponse> myRead1 = getNotificationDetailsRequest(
        accessToken, "my-event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> myRead2 = getNotificationDetailsRequest(
        accessToken, "my-event2", status().isOk());
    
    assert myRead1.getData().isRead();
    assert myRead2.getData().isRead();
    
    // 다른 사용자 토큰으로 다른 사용자의 알림 상태 확인
    String otherUserToken = accessTokenTestUtils.generateAccessToken(OTHER_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    CommonResponse<GetNotificationDetailsApiResponse> otherRead1 = getNotificationDetailsRequest(
        otherUserToken, "other-event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> otherRead2 = getNotificationDetailsRequest(
        otherUserToken, "other-event2", status().isOk());
    
    assert !otherRead1.getData().isRead(); // 읽지 않음 상태 유지
    assert !otherRead2.getData().isRead(); // 읽지 않음 상태 유지
  }

  @Order(5)
  @Test
  @DisplayName("5. 인증 없이 전체 읽음 처리 - 400 Bad Request")
  void markAllAsReadNotifications_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";
    
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        emptyToken, status().isBadRequest()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(6)
  @Test
  @DisplayName("6. 만료된 토큰으로 전체 읽음 처리 - 401 Unauthorized")
  void markAllAsReadNotifications_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        expiredToken, status().isUnauthorized()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
  }

  @Order(7)
  @Test
  @DisplayName("7. 혼합된 읽음 상태의 알림들 전체 읽음 처리 - 성공")
  void markAllAsReadNotifications_WithMixedReadStatus_ShouldMarkAllAsRead() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    NotificationEntity notification1 = createTestNotification(VALID_USER_ID, "mixed-event1", "post1", "liker1");
    NotificationEntity notification2 = createTestNotification(VALID_USER_ID, "mixed-event2", "post2", "liker2");
    NotificationEntity notification3 = createTestNotification(VALID_USER_ID, "mixed-event3", "post3", "liker3");
    
    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
    notificationRepository.save(notification3);
    
    // 일부 알림만 읽음 처리
    markAsReadNotificationRequest(accessToken, "mixed-event1", status().isOk());
    
    // 읽음 상태가 혼재된 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> mixedCheck1 = getNotificationDetailsRequest(
        accessToken, "mixed-event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> mixedCheck2 = getNotificationDetailsRequest(
        accessToken, "mixed-event2", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> mixedCheck3 = getNotificationDetailsRequest(
        accessToken, "mixed-event3", status().isOk());
    
    assert mixedCheck1.getData().isRead();   // 읽음
    assert !mixedCheck2.getData().isRead();  // 읽지 않음
    assert !mixedCheck3.getData().isRead();  // 읽지 않음

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 모든 알림이 읽음 상태로 변경되었는지 확인
    CommonResponse<GetNotificationDetailsApiResponse> finalCheck1 = getNotificationDetailsRequest(
        accessToken, "mixed-event1", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> finalCheck2 = getNotificationDetailsRequest(
        accessToken, "mixed-event2", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> finalCheck3 = getNotificationDetailsRequest(
        accessToken, "mixed-event3", status().isOk());
    
    assert finalCheck1.getData().isRead();
    assert finalCheck2.getData().isRead();
    assert finalCheck3.getData().isRead();
  }

  @Order(8)
  @Test
  @DisplayName("8. 대량의 알림 전체 읽음 처리 - 성공")
  void markAllAsReadNotifications_WithLargeAmountOfNotifications_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 대량 알림 생성 (30개)
    for (int i = 0; i < 30; i++) {
      NotificationEntity notification = createTestNotification(VALID_USER_ID, "bulk-event-" + i, 
          "bulk-post-" + i, "bulk-liker-" + i);
      notificationRepository.save(notification);
    }

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 샘플링으로 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> bulkCheck1 = getNotificationDetailsRequest(
        accessToken, "bulk-event-0", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> bulkCheck2 = getNotificationDetailsRequest(
        accessToken, "bulk-event-15", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> bulkCheck3 = getNotificationDetailsRequest(
        accessToken, "bulk-event-29", status().isOk());
    
    assert bulkCheck1.getData().isRead();
    assert bulkCheck2.getData().isRead();
    assert bulkCheck3.getData().isRead();
  }

  @Order(9)
  @Test
  @DisplayName("9. 전체 읽음 처리 후 목록 조회에서 모든 알림이 읽음 상태 - 성공")
  void markAllAsReadNotifications_AfterMarkingAllAsRead_ShouldShowAllReadInList() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 여러 알림 생성
    for (int i = 0; i < 5; i++) {
      NotificationEntity notification = createTestNotification(VALID_USER_ID, "list-event-" + i, 
          "list-post-" + i, "list-liker-" + i);
      notificationRepository.save(notification);
    }

    // when
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );

    // then
    assert response.isSuccess();
    
    // 목록 조회로 모든 알림이 읽음 상태인지 확인
    CommonResponse<CursorPageApiResponse<NotificationDetails>> listResponse = 
        getNotificationsByCursorRequest(accessToken, null, null, 10, status().isOk());
    
    assert listResponse.isSuccess();
    List<NotificationDetails> notifications = listResponse.getData().content();
    assert notifications.size() == 5;
    
    // 모든 알림이 읽음 상태인지 확인
    assert notifications.stream().allMatch(NotificationDetails::isRead);
  }

  @Order(10)
  @Test
  @DisplayName("10. 전체 읽음 처리 성능 및 안정성 검증")
  void markAllAsReadNotifications_PerformanceAndStabilityTest() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // 다양한 패턴의 알림 생성
    for (int i = 0; i < 25; i++) {
      NotificationEntity notification = createTestNotification(VALID_USER_ID, "perf-event-" + i, 
          "perf-post-" + i, "perf-liker-" + i);
      notificationRepository.save(notification);
    }

    // when
    long startTime = System.currentTimeMillis();
    CommonResponse<Void> response = markAllAsReadNotificationsRequest(
        accessToken, status().isOk()
    );
    long endTime = System.currentTimeMillis();

    // then
    assert response.isSuccess();
    
    // 성능 검증 (1초 이내 완료)
    assert (endTime - startTime) < 1000;
    
    // 샘플링으로 읽음 상태 확인
    CommonResponse<GetNotificationDetailsApiResponse> perfCheck1 = getNotificationDetailsRequest(
        accessToken, "perf-event-0", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> perfCheck2 = getNotificationDetailsRequest(
        accessToken, "perf-event-24", status().isOk());
    
    assert perfCheck1.getData().isRead();
    assert perfCheck2.getData().isRead();
  }
}