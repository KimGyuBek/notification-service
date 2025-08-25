package com.threadly.notification.adapter.persistence.notification.repository;

import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoNotificationRepository extends MongoRepository<NotificationEntity, String> {

  /**
   * 주어진 eventId, receiverId에 해당하는 데이터가 있는지 조회
   *
   * @param eventId
   * @param receiverId
   * @return
   */
  boolean existsByEventIdAndReceiverId(String eventId, String receiverId);

  /**
   * 주어진 receiverId에 해당하는 사용자의 데이터 전체 삭제
   * @param receiverId
   */
  void deleteAllByReceiverId(String receiverId);

//  // 사용자별 알림 조회
//  List<NotificationEntity> findByUserId(String userId);
//
//  // 읽지 않은 알림 조회
//  List<NotificationEntity> findByUserIdAndIsReadFalse(String userId);
//
//  // 특정 게시물의 좋아요 알림 조회
//  @Query("{'postId': ?0, 'notificationType': 'LIKE'}")
//  List<NotificationEntity> findLikeNotificationsByPostId(String postId);
//
//  // 사용자별 알림 개수
//  long countByUserIdAndIsReadFalse(String userId);
//
//  // 알림 타입별 조회
//  List<NotificationEntity> findByUserIdAndNotificationType(String userId, String notificationType);
}