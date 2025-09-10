package com.threadly.notification.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowAcceptMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationDetailsApiResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NotificationType별 메타데이터 JsonSubTypes 저장 테스트")
public class NotificationMetadataSerializationTest extends BaseNotificationApiTest {

  private static final String VALID_USER_ID = "user123";
  private static final String ACTOR_USER_ID = "actor456";
  private static final String USER_TYPE = "USER";
  private static final String USER_STATUS_TYPE = "ACTIVE";

  @AfterEach
  void tearDown() {
    notificationRepository.deleteAll();
  }

  @Test
  @DisplayName("POST_LIKE 메타데이터 저장 및 조회 테스트")
  void testPostLikeMetadata() throws Exception {
    String eventId = "post-like-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.POST_LIKE, createPostLikeMetadata("post123")
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.POST_LIKE;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.POST_LIKE;
    
    // 메타데이터 상세 검증
    PostLikeMeta metadata = (PostLikeMeta) response.getData().metaData();
    assert metadata != null;
    assert metadata.postId().equals("post123");
  }

  @Test
  @DisplayName("COMMENT_ADDED 메타데이터 저장 및 조회 테스트")
  void testCommentAddedMetadata() throws Exception {
    String eventId = "comment-added-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.COMMENT_ADDED, createPostCommentMetadata("post456", "comment789", "좋은 글이네요!")
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.COMMENT_ADDED;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.COMMENT_ADDED;
    
    // 메타데이터 상세 검증
    PostCommentMeta metadata = (PostCommentMeta) response.getData().metaData();
    assert metadata != null;
    assert metadata.postId().equals("post456");
    assert metadata.commentId().equals("comment789");
    assert metadata.commentExcerpt().equals("좋은 글이네요!");
  }

  @Test
  @DisplayName("COMMENT_LIKE 메타데이터 저장 및 조회 테스트")
  void testCommentLikeMetadata() throws Exception {
    String eventId = "comment-like-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.COMMENT_LIKE, createCommentLikeMetadata("post789", "comment123", "멋진 댓글!")
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.COMMENT_LIKE;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.COMMENT_LIKE;
    
    // 메타데이터 상세 검증
    CommentLikeMeta metadata = (CommentLikeMeta) response.getData().metaData();
    assert metadata != null;
    assert metadata.postId().equals("post789");
    assert metadata.commentId().equals("comment123");
    assert metadata.commentExcerpt().equals("멋진 댓글!");
  }

  @Test
  @DisplayName("FOLLOW_REQUEST 메타데이터 저장 및 조회 테스트")
  void testFollowRequestMetadata() throws Exception {
    String eventId = "follow-request-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.FOLLOW_REQUEST, createFollowRequestMetadata()
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.FOLLOW_REQUEST;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.FOLLOW_REQUEST;
    
    // 메타데이터 상세 검증 (FollowRequestMeta는 필드가 없음)
    FollowRequestMeta metadata = (FollowRequestMeta) response.getData().metaData();
    assert metadata != null;
  }

  @Test
  @DisplayName("FOLLOW 메타데이터 저장 및 조회 테스트")
  void testFollowMetadata() throws Exception {
    String eventId = "follow-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.FOLLOW, createFollowMetadata()
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.FOLLOW;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.FOLLOW;
    
    // 메타데이터 상세 검증 (FollowMeta는 필드가 없음)
    FollowMeta metadata = (FollowMeta) response.getData().metaData();
    assert metadata != null;
  }

  @Test
  @DisplayName("FOLLOW_ACCEPT 메타데이터 저장 및 조회 테스트")
  void testFollowAcceptMetadata() throws Exception {
    String eventId = "follow-accept-meta-test";
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE, USER_STATUS_TYPE);
    
    NotificationEvent event = createNotificationEvent(
        eventId, NotificationType.FOLLOW_ACCEPT, createFollowAcceptMetadata()
    );

    sendKafkaTest(event, status().isOk());

    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          Optional<NotificationDoc> savedEntity = notificationRepository.findByEventIdAndReceiverId(eventId, VALID_USER_ID);
          assert savedEntity.isPresent();
          assert savedEntity.get().getNotificationType() == NotificationType.FOLLOW_ACCEPT;
        });

    CommonResponse<GetNotificationDetailsApiResponse> response = getNotificationDetailsRequest(
        accessToken, eventId, status().isOk());
    
    assert response.isSuccess();
    assert response.getData().notificationType() == NotificationType.FOLLOW_ACCEPT;
    
    // 메타데이터 상세 검증 (FollowAcceptMeta는 필드가 없음)
    FollowAcceptMeta metadata = (FollowAcceptMeta) response.getData().metaData();
    assert metadata != null;
  }

  private NotificationEvent createNotificationEvent(String eventId, NotificationType type, Map<String, Object> metadata) {
    ActorProfile actorProfile = new ActorProfile(ACTOR_USER_ID, "테스터", "https://test.com/profile.jpg");
    
    return new NotificationEvent(
        eventId,
        VALID_USER_ID,
        type,
        LocalDateTime.now(),
        actorProfile,
        metadata
    );
  }

  private Map<String, Object> createPostLikeMetadata(String postId) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "POST_LIKE");
    metadata.put("postId", postId);
    return metadata;
  }

  private Map<String, Object> createPostCommentMetadata(String postId, String commentId, String commentExcerpt) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "COMMENT_ADDED");
    metadata.put("postId", postId);
    metadata.put("commentId", commentId);
    metadata.put("commentExcerpt", commentExcerpt);
    return metadata;
  }

  private Map<String, Object> createCommentLikeMetadata(String postId, String commentId, String commentExcerpt) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "COMMENT_LIKE");
    metadata.put("postId", postId);
    metadata.put("commentId", commentId);
    metadata.put("commentExcerpt", commentExcerpt);
    return metadata;
  }

  private Map<String, Object> createFollowRequestMetadata() {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "FOLLOW_REQUEST");
    return metadata;
  }

  private Map<String, Object> createFollowMetadata() {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "FOLLOW");
    return metadata;
  }

  private Map<String, Object> createFollowAcceptMetadata() {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", "FOLLOW_ACCEPT");
    return metadata;
  }
}