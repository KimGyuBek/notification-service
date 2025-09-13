package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.user.ActorProfile;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Kafka 이벤트 수신 및 DB 저장 통합 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationKafkaIntegrationTest extends BaseNotificationApiTest {

  private static final String VALID_USER_ID = "user123";
  private static final String ACTOR_USER_ID = "actor456";
  private static final String USER_TYPE = "USER";
  private static final String USER_STATUS_TYPE = "ACTIVE";

  @AfterEach
  void tearDown() {
    notificationRepository.deleteAll();
  }

  @Order(1)
  @Test
  @DisplayName("1. POST_LIKE 이벤트 수신 및 DB 저장 - 성공")
  void kafkaIntegration_PostLikeEvent_ShouldSaveToDatabase() throws Exception {
    // given
    String eventId = "kafka-test-event-001";
    String postId = "test-post-123";
    String likerId = "liker-789";
    
    NotificationEvent event = createPostLikeEvent(
        eventId, VALID_USER_ID, ACTOR_USER_ID, "좋아요러", "https://test.com/profile.jpg", postId
    );

    // when - Kafka 이벤트 발신
    CommonResponse<Void> kafkaResponse = sendKafkaTest(event, status().isOk());

    // then - Kafka 발신 성공 확인
    assert kafkaResponse.isSuccess();

    // DB 저장 확인 (비동기 처리로 인한 대기)
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(
              eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          
          NotificationDoc notification = savedEntity.get();
          assert notification.getEventId().equals(eventId);
          assert notification.getReceiverId().equals(VALID_USER_ID);
          assert notification.getNotificationType() == NotificationType.POST_LIKE;
          assert !notification.isRead(); // 기본적으로 읽지 않음
          assert notification.getActorProfile() != null;
          assert notification.getActorProfile().getUserId().equals(ACTOR_USER_ID);
        });
  }

  @Order(2)
  @Test
  @DisplayName("2. 저장된 알림 API 조회로 검증 - 성공")
  void kafkaIntegration_SavedNotificationApiVerification_ShouldReturnCorrectData() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    String eventId = "kafka-api-test-002";
    String postId = "api-test-post-456";
    String likerId = "api-liker-123";
    
    NotificationEvent event = createPostLikeEvent(
        eventId, VALID_USER_ID, ACTOR_USER_ID, "API테스터", "https://api-test.com/profile.jpg", postId
    );

    // when - Kafka 이벤트 발신
    sendKafkaTest(event, status().isOk());

    // 저장 대기
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          assert notificationRepository.existsByEventIdAndReceiverId(eventId, VALID_USER_ID);
        });

    // API로 조회
    CommonResponse<GetNotificationDetailsApiResponse> apiResponse = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk()
    );

    // then - API 응답 검증
    assert apiResponse.isSuccess();
    GetNotificationDetailsApiResponse data = apiResponse.getData();
    
    assert data.eventId().equals(eventId);
    assert data.receiverId().equals(VALID_USER_ID);
    assert data.notificationType() == NotificationType.POST_LIKE;
    assert !data.isRead();
    
    // ActorProfile 검증
    ActorProfile actorProfile = data.actorProfile();
    assert actorProfile != null;
    assert actorProfile.getUserId().equals(ACTOR_USER_ID);
    assert actorProfile.getNickname().equals("API테스터");
    assert actorProfile.getProfileImageUrl().equals("https://api-test.com/profile.jpg");
  }

  @Order(3)
  @Test
  @DisplayName("3. 다중 이벤트 수신 및 개별 조회 - 성공")
  void kafkaIntegration_MultipleEvents_ShouldSaveAllAndRetrieveIndividually() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    String eventId1 = "multi-event-001";
    String eventId2 = "multi-event-002";
    String eventId3 = "multi-event-003";
    
    NotificationEvent event1 = createPostLikeEvent(eventId1, VALID_USER_ID, "actor1", "액터1", "https://actor1.com/profile.jpg", "post1");
    NotificationEvent event2 = createPostLikeEvent(eventId2, VALID_USER_ID, "actor2", "액터2", "https://actor2.com/profile.jpg", "post2");
    NotificationEvent event3 = createPostLikeEvent(eventId3, VALID_USER_ID, "actor3", "액터3", "https://actor3.com/profile.jpg", "post3");

    // when - 다중 이벤트 발신
    sendKafkaTest(event1, status().isOk());
    sendKafkaTest(event2, status().isOk());
    sendKafkaTest(event3, status().isOk());

    // 모든 이벤트 저장 대기
    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          assert notificationRepository.existsByEventIdAndReceiverId(eventId1, VALID_USER_ID);
          assert notificationRepository.existsByEventIdAndReceiverId(eventId2, VALID_USER_ID);
          assert notificationRepository.existsByEventIdAndReceiverId(eventId3, VALID_USER_ID);
        });

    // then - 개별 조회로 검증
    CommonResponse<GetNotificationDetailsApiResponse> response1 = getNotificationDetailsRequest(
        accessToken, eventId1, status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> response2 = getNotificationDetailsRequest(
        accessToken, eventId2, status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> response3 = getNotificationDetailsRequest(
        accessToken, eventId3, status().isOk());

    // 각각 다른 ActorProfile 정보 확인
    assert response1.getData().actorProfile().getNickname().equals("액터1");
    assert response2.getData().actorProfile().getNickname().equals("액터2");
    assert response3.getData().actorProfile().getNickname().equals("액터3");
  }

  @Order(4)
  @Test
  @DisplayName("4. 잘못된 이벤트 데이터 처리 - Kafka 발신 성공하지만 저장 실패")
  void kafkaIntegration_InvalidEventData_ShouldHandleGracefully() throws Exception {
    // given
    String eventId = "invalid-event-004";
    
    // receiverUserId를 null로 설정한 잘못된 이벤트
    NotificationEvent invalidEvent = new NotificationEvent(
        eventId,
        null, // 잘못된 데이터
        NotificationType.POST_LIKE,
        LocalDateTime.now(),
        new ActorProfile(ACTOR_USER_ID, "테스터", "https://test.com/profile.jpg"),
        createPostLikeMetadata("test-post")
    );

    // when - 잘못된 이벤트 발신
    CommonResponse<Void> kafkaResponse = sendKafkaTest(invalidEvent, status().isOk());

    // then - Kafka 발신은 성공하지만 DB에 저장되지 않음
    assert kafkaResponse.isSuccess();

    // 저장되지 않음을 확인
    Thread.sleep(3000); // 처리 시간 대기
    assert !notificationRepository.existsByEventIdAndReceiverId(eventId, VALID_USER_ID);
  }

  @Order(5)
  @Test
  @DisplayName("5. 동일한 eventId 중복 이벤트 처리 - 멱등성 확인")
  void kafkaIntegration_DuplicateEventId_ShouldHandleIdempotently() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    String duplicateEventId = "duplicate-event-005";
    
    NotificationEvent event = createPostLikeEvent(
        duplicateEventId, VALID_USER_ID, ACTOR_USER_ID, "중복테스터", "https://duplicate.com/profile.jpg", "duplicate-post"
    );

    // when - 동일한 이벤트 두 번 발신
    sendKafkaTest(event, status().isOk());
    sendKafkaTest(event, status().isOk());

    // 저장 대기
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          assert notificationRepository.existsByEventIdAndReceiverId(duplicateEventId, VALID_USER_ID);
        });

    // then - 한 번만 저장되었는지 확인 (API로 검증)
    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, duplicateEventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().eventId().equals(duplicateEventId);
  }

  @Order(6)
  @Test
  @DisplayName("6. 대용량 이벤트 처리 - 성능 및 안정성 검증")
  void kafkaIntegration_BulkEvents_ShouldProcessAllSuccessfully() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    int eventCount = 10;

    // when - 대용량 이벤트 발신
    for (int i = 0; i < eventCount; i++) {
      String eventId = "bulk-event-" + String.format("%03d", i);
      NotificationEvent event = createPostLikeEvent(
          eventId, VALID_USER_ID, "bulk-actor-" + i, "벌크액터" + i, 
          "https://bulk.com/" + i + "/profile.jpg", "bulk-post-" + i
      );
      sendKafkaTest(event, status().isOk());
    }

    // 모든 이벤트 저장 대기
    Awaitility.await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          for (int i = 0; i < eventCount; i++) {
            String eventId = "bulk-event-" + String.format("%03d", i);
            assert notificationRepository.existsByEventIdAndReceiverId(eventId, VALID_USER_ID);
          }
        });

    // then - 샘플링으로 API 조회 검증
    CommonResponse<GetNotificationDetailsApiResponse> firstResponse = getNotificationDetailsRequest(
        accessToken, "bulk-event-000", status().isOk());
    CommonResponse<GetNotificationDetailsApiResponse> lastResponse = getNotificationDetailsRequest(
        accessToken, "bulk-event-009", status().isOk());

    assert firstResponse.isSuccess();
    assert lastResponse.isSuccess();
    assert firstResponse.getData().actorProfile().getNickname().equals("벌크액터0");
    assert lastResponse.getData().actorProfile().getNickname().equals("벌크액터9");
  }

  /**
   * POST_LIKE 이벤트 생성 헬퍼 메서드
   */
  private NotificationEvent createPostLikeEvent(String eventId, String receiverUserId, 
      String actorUserId, String actorNickname, String actorProfileUrl, String postId) {
    
    ActorProfile actorProfile = new ActorProfile(actorUserId, actorNickname, actorProfileUrl);
    Map<String, Object> metadata = createPostLikeMetadata(postId);
    
    return new NotificationEvent(
        eventId,
        receiverUserId,
        NotificationType.POST_LIKE,
        LocalDateTime.now(),
        actorProfile,
        metadata
    );
  }

  /**
   * POST_LIKE 메타데이터 생성 헬퍼 메서드
   */
  private Map<String, Object> createPostLikeMetadata(String postId) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "POST_LIKE");
    metadata.put("postId", postId);
    return metadata;
  }
}