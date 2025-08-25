package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Notification 조회 관련 API 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetNotificationApiTest extends BaseNotificationApiTest {


  private static final String VALID_USER_ID = "user123";
  private static final String OTHER_USER_ID = "user456";
  private static final String USER_TYPE = "USER";
  private static final String USER_STATUS_TYPE = "ACTIVE";

  @AfterEach
  void tearDown() {
    // 테스트 후 데이터 정리
    notificationRepository.deleteAll();
  }

  @Order(1)
  @Test
  @DisplayName("1. 존재하는 알림 조회 - 성공")
  void getNotificationDetail_ExistingNotification_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    assert data.eventId().equals(notification.getEventId());
    assert data.receiverId().equals(VALID_USER_ID);
    assert data.notificationType() == NotificationType.POST_LIKE;
    assert !data.isRead(); // 기본적으로 읽지 않음
    
    // ActorProfile 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.userId().equals("test-liker");
    assert actorProfile.nickname().equals("test-nickname");
    assert actorProfile.profileImageUrl().equals("https://test.com/profile.jpg");
  }

  @Order(2)
  @Test
  @DisplayName("2. 존재하지 않는 알림 조회 - 404 Not Found")
  void getNotificationDetail_NonExistentNotification_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String nonExistentEventId = "non-existent-event-id";

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, nonExistentEventId, status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(3)
  @Test
  @DisplayName("3. 다른 사용자의 알림 조회 - 403 Forbidden")
  void getNotificationDetail_OtherUserNotification_ShouldReturnForbidden() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity otherUserNotification = createTestNotification(OTHER_USER_ID);
    notificationRepository.save(otherUserNotification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, otherUserNotification.getEventId(), status().isForbidden()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_ACCESS_FORBIDDEN);
  }

  @Order(4)
  @Test
  @DisplayName("4. 인증 없이 알림 조회 - 400 Bad Request")
  void getNotificationDetail_WithoutAuthentication_ShouldReturnBadRequest() throws Exception {
    // given
    String emptyToken = "";
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        emptyToken, notification.getEventId(), status().isBadRequest()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(5)
  @Test
  @DisplayName("5. 만료된 토큰으로 알림 조회 - 401 Unauthorized")
  void getNotificationDetail_WithExpiredToken_ShouldReturnUnauthorized() throws Exception {
    // given
    String expiredToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    NotificationEntity notification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        expiredToken, notification.getEventId(), status().isUnauthorized()
    );

    // then
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);
  }

  @Order(6)
  @Test
  @DisplayName("6. 특수 문자가 포함된 eventId로 알림 조회 - 404 Not Found")
  void getNotificationDetail_WithSpecialCharacterEventId_ShouldReturnNotFound() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String specialEventId = "event-id-with-special-chars!@#$%";

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, specialEventId, status().isNotFound()
    );

    // then
    validateFailResponse(response, ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  @Order(7)
  @Test
  @DisplayName("7. 여러 알림 중 특정 알림 조회 - 성공")
  void getNotificationDetail_MultipleNotificationsExist_ShouldReturnCorrectOne() throws Exception {
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

    // when - 두 번째 알림 조회
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, notification2.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    assert data.eventId().equals("event2");
    PostLikeMeta metadata = (PostLikeMeta) data.metaData();
    assert metadata.postId().equals("post2");
    assert metadata.likerId().equals("liker2");
    
    // ActorProfile 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.userId().equals("liker2");
    assert actorProfile.nickname().equals("test-nickname");
    assert actorProfile.profileImageUrl().equals("https://test.com/profile.jpg");
  }

  @Order(8)
  @Test
  @DisplayName("8. 다양한 알림 타입 조회 - 성공")
  void getNotificationDetail_DifferentNotificationTypes_ShouldReturnSuccess() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // POST_LIKE 타입 알림
    NotificationEntity postLikeNotification = createTestNotification(VALID_USER_ID);
    notificationRepository.save(postLikeNotification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, postLikeNotification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    assert data.notificationType() == NotificationType.POST_LIKE;
    assert data.metaData() instanceof PostLikeMeta;
    
    // ActorProfile 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.userId().equals("test-liker");
    assert actorProfile.nickname().equals("test-nickname");
    assert actorProfile.profileImageUrl().equals("https://test.com/profile.jpg");
  }

  @Order(9)
  @Test
  @DisplayName("9. 정상적인 메타데이터 조회 - 성공")
  void getNotificationDetail_WithMetadata_ShouldReturnCorrectMetadata() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String testPostId = "test-post-123";
    String testLikerId = "liker-456";

    NotificationEntity notification = createTestNotification(VALID_USER_ID, "test-event",
        testPostId, testLikerId);
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    PostLikeMeta metadata = (PostLikeMeta) data.metaData();
    assert metadata.postId().equals(testPostId);
    assert metadata.likerId().equals(testLikerId);
    
    // ActorProfile 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.userId().equals(testLikerId);
    assert actorProfile.nickname().equals("test-nickname");
    assert actorProfile.profileImageUrl().equals("https://test.com/profile.jpg");
  }

  @Order(10)
  @Test
  @DisplayName("10. 다양한 ActorProfile 정보가 포함된 알림 조회 - 성공")
  void getNotificationDetail_WithDifferentActorProfile_ShouldReturnCorrectProfile() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String customNickname = "커스텀닉네임";
    String customProfileUrl = "https://custom.com/profile/image.jpg";
    
    NotificationEntity notification = createTestNotification(VALID_USER_ID, "custom-event", 
        "custom-post", "custom-liker", customNickname, customProfileUrl);
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    
    // ActorProfile 상세 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.userId().equals("custom-liker");
    assert actorProfile.nickname().equals(customNickname);
    assert actorProfile.profileImageUrl().equals(customProfileUrl);
  }

  @Order(11)
  @Test
  @DisplayName("11. null ActorProfile로 알림 조회 - ActorProfile 필드 검증")
  void getNotificationDetail_WithNullActorProfile_ShouldHandleGracefully() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    
    // ActorProfile이 null인 알림 생성
    PostLikeMeta metadata = new PostLikeMeta("test-post", "test-liker");
    NotificationEntity notification = new NotificationEntity(
        "null-profile-event",
        VALID_USER_ID,
        NotificationType.POST_LIKE,
        metadata,
        LocalDateTime.now(),
        null  // ActorProfile을 null로 설정
    );
    notificationRepository.save(notification);

    // when
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, notification.getEventId(), status().isOk()
    );

    // then
    assert response.isSuccess();
    GetNotificationDetailsApiResponse data = response.getData();
    
    // ActorProfile null 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile == null;  // null 허용하는 경우
  }

}