package com.threadly.notification.adapter.persistence.notification.repository;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * 커서 기반 notification 조회 repository
 */
@Repository
@RequiredArgsConstructor
public class NotificationCustomRepository {

  private final MongoTemplate mongoTemplate;

  /**
   * 주어진 eventId에 해당하는 알림 데이터릐 isRead 업데이트
   *
   * @param eventId
   * @param isRead
   */
  public void updateIsReadByEventId(String eventId, boolean isRead) {
    Query query = new Query(Criteria.where("eventId").is(eventId));
    Update update = new Update().set("isRead", isRead);
    mongoTemplate.updateFirst(query, update, NotificationDoc.class);
  }

  /**
   * 주어진 receiverId에 해당하는 알림 데이터 읽음 처리
   *
   * @param receiverId
   */
  public void updateAllIsReadByReceiverId(String receiverId) {
    Query query = new Query(Criteria.where("receiverId").is(receiverId));
    Update update = new Update().set("isRead", true);
    mongoTemplate.updateMulti(query, update, NotificationDoc.class);
  }

  /**
   * 알림 목록 커서 기반 조회
   *
   * @param getNotificationsQuery
   * @return
   */
  public List<NotificationDoc> findNotificationsByCursor(
      GetNotificationsQuery getNotificationsQuery) {
    Query query = new Query();

    query.addCriteria(Criteria.where("receiverId").is(getNotificationsQuery.userId()));

    // 커서 기반 페이징: createdAt과 _id를 함께 사용
    if (getNotificationsQuery.cursorTimestamp() != null
        && getNotificationsQuery.cursorId() != null) {
      Criteria cursorCriteria = new Criteria().orOperator(
          // createdAt이 커서보다 작거나
          Criteria.where("occurredAt").lt(getNotificationsQuery.cursorTimestamp()),
          // createdAt이 같고 _id가 커서보다 작은 경우
          new Criteria().andOperator(
              Criteria.where("occurredAt").is(getNotificationsQuery.cursorTimestamp()),
              Criteria.where("eventId").lt(getNotificationsQuery.cursorId())
          )
      );
      query.addCriteria(cursorCriteria);
    } else if (getNotificationsQuery.cursorTimestamp() != null) {
      // cursorId가 없으면 timestamp만 사용
      query.addCriteria(Criteria.where("occurredAt").lt(getNotificationsQuery.cursorTimestamp()));
    }

    // 정렬: occurredAt 내림차순, _id 내림차순 (같은 시간대에서는 _id로 구분)
    query.with(Sort.by(Direction.DESC, "occurredAt").and(Sort.by(Direction.DESC, "eventId")));
    query.limit(getNotificationsQuery.limit() + 1);

    return mongoTemplate.find(query, NotificationDoc.class);
  }

}
