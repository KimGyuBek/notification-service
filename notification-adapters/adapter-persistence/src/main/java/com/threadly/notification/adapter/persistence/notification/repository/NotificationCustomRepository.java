package com.threadly.notification.adapter.persistence.notification.repository;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import java.time.LocalDateTime;
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
    var criteria = Criteria.where("receiverId").is(getNotificationsQuery.userId());

    return mongoTemplate.find(applyCursorCriteria(getNotificationsQuery.cursorTimestamp(),
            getNotificationsQuery.cursorId(), getNotificationsQuery.limit(), criteria),
        NotificationDoc.class);
  }

  /**
   * 읽지 않은 알림 목록 커서 기반 조회
   *
   * @param getNotificationsQuery
   * @return
   */
  public List<NotificationDoc> findUnreadByCursor(GetNotificationsQuery getNotificationsQuery) {

    var criteria = Criteria.where("receiverId").is(getNotificationsQuery.userId())
        .and("isRead").is(false);

    return mongoTemplate.find(applyCursorCriteria(getNotificationsQuery.cursorTimestamp(),
            getNotificationsQuery.cursorId(), getNotificationsQuery.limit(), criteria),
        NotificationDoc.class);
  }

  private Query applyCursorCriteria(LocalDateTime cursorTimestamp, String cursorId, int limit,
      Criteria base) {
    Criteria root = base;

    if (cursorTimestamp != null
        && cursorId != null) {
      Criteria cursorCriteria = new Criteria().orOperator(
          Criteria.where("occurredAt").lt(cursorTimestamp),
          new Criteria().andOperator(
              Criteria.where("occurredAt").is(cursorTimestamp),
              Criteria.where("eventId").lt(cursorId))
      );

      root.andOperator(base, cursorCriteria);
    } else if (cursorTimestamp != null) {
      Criteria cursorcCriteria = new Criteria().where("occurredAt").lt(cursorTimestamp);
      root.andOperator(base, cursorcCriteria);
    }

    return Query.query(base)
        .with(Sort.by(Direction.DESC, "occurredAt").and(Sort.by(Direction.DESC, "eventId")))
        .limit(limit + 1);
  }


}
