package com.threadly.notification.adapter.persistence.notification.repository;

import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * 커서 기반 notification 조회 repository
 */
@Repository
@RequiredArgsConstructor
public class NotificationCursorRepository {

  private final MongoTemplate mongoTemplate;

  public List<NotificationEntity> findNotificationsByCursor(
      GetNotificationsQuery getNotificationsQuery) {
    Query query = new Query();

    query.addCriteria(Criteria.where("receiverId").is(getNotificationsQuery.userId()));

    // 커서 기반 페이징: createdAt과 _id를 함께 사용
    if (getNotificationsQuery.cursorTimestamp() != null && getNotificationsQuery.cursorId() != null) {
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

    // 정렬: createdAt 내림차순, _id 내림차순 (같은 시간대에서는 _id로 구분)
    query.with(Sort.by(Direction.DESC, "occurredAt").and(Sort.by(Direction.DESC, "eventId")));
    query.limit(getNotificationsQuery.limit() + 1);

    return mongoTemplate.find(query, NotificationEntity.class);
  }

}
