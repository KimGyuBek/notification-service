package com.threadly.notification.adapter.persistence.notification.mapper;

import com.mongodb.connection.ServerVersion;
import com.threadly.notification.adapter.persistence.notification.entity.NotificationEntity;
import com.threadly.notification.core.domain.notification.Notification;

public class NotificationMapper {

  /**
   * domain -> entity
   *
   * @param domain
   * @return
   */
  public static NotificationEntity toEntity(Notification domain) {
    return new NotificationEntity(
        domain.getEventId(),
        domain.getReceiverId(),
        domain.getNotificationType(),
        domain.getMetadata(),
        domain.getOccurredAt()
    );
  }

  /**
   * entity -> domain
   * @param entity
   * @return
   */
  public static Notification toDomain(NotificationEntity entity) {
    return new Notification(
        entity.getEventId(),
        entity.getReceiverId(),
        entity.getNotificationType(),
        entity.getOccurredAt(),
        entity.isRead(),
        entity.getMetadata()
    );
  }

}
