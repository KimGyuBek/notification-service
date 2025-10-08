package com.threadly.notification.adapter.persistence.notification.repository;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMongoRepository extends MongoRepository<NotificationDoc, String> {

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

  /**
   * 주어진 eventId, receiverId에 해당하는 알림 데이터 조회
   * @param eventId
   * @param receiverId
   * @return
   */
  Optional<NotificationDoc> findByEventIdAndReceiverId(String eventId, String receiverId);
}