package com.threadly.notification.adapter.persistence.notification.adapter;

import com.threadly.notification.adapter.persistence.notification.doc.NotificationDoc;
import com.threadly.notification.adapter.persistence.notification.mapper.NotificationMapper;
import com.threadly.notification.adapter.persistence.notification.repository.NotificationMongoRepository;
import com.threadly.notification.adapter.persistence.notification.repository.NotificationCustomRepository;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import com.threadly.notification.core.port.notification.out.dto.SavedNotificationEventDoc;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceAdapter implements NotificationCommandPort,
    NotificationQueryPort {

  private final NotificationMongoRepository notificationMongoRepository;
  private final NotificationCustomRepository notificationCustomRepository;

  @Override
  public SavedNotificationEventDoc save(Notification notification) {
    NotificationDoc saved = notificationMongoRepository.save(
        NotificationDoc.newDoc(
            notification.getEventId(),
            notification.getReceiverId(),
            new ObjectId().toHexString(),
            notification.getNotificationType(),
            notification.getMetadata(),
            notification.getOccurredAt(),
            notification.getActorProfile()
        )
    );

    return new SavedNotificationEventDoc(
        saved.getEventId(),
        saved.getSortId(),
        saved.getNotificationType(),
        saved.getMetadata(),
        saved.getOccurredAt()
    );
  }

  @Override
  public Optional<Notification> fetchByEventId(String eventId) {
    return
        notificationMongoRepository.findById(eventId).map(
            NotificationMapper::toDomain
        );
  }

  @Override
  public List<NotificationDetails> fetchAllByCursor(GetNotificationsQuery query) {
    return notificationCustomRepository.findNotificationsByCursor(query).stream().map(
        entity -> new NotificationDetails(
            entity.getEventId(),
            entity.getSortId(),
            entity.getReceiverId(),
            entity.getNotificationType(),
            entity.getOccurredAt(),
            entity.getActorProfile(),
            entity.isRead()
        )
    ).collect(Collectors.toList());
  }

  @Override
  public List<NotificationDetails> fetchUnreadByCursor(GetNotificationsQuery query) {
    return notificationCustomRepository.findUnreadByCursor(query).stream().map(
        entity -> new NotificationDetails(
            entity.getEventId(),
            entity.getSortId(),
            entity.getReceiverId(),
            entity.getNotificationType(),
            entity.getOccurredAt(),
            entity.getActorProfile(),
            entity.isRead()
        )
    ).collect(Collectors.toList());
  }

  @Override
  public void deleteByEventId(String eventId) {
    notificationMongoRepository.deleteById(eventId);
  }

  @Override
  public boolean existsByEventIdAndReceiverId(String eventId, String receiverId) {
    return
        notificationMongoRepository.existsByEventIdAndReceiverId(eventId, receiverId);
  }

  @Override
  public void deleteAllByReceiverId(String receiverId) {
    notificationMongoRepository.deleteAllByReceiverId(receiverId);
  }

  @Override
  public Optional<Notification> fetchByEventIdAndReceiverId(String eventId, String receiverId) {
    return notificationMongoRepository.findByEventIdAndReceiverId(eventId, receiverId)
        .map(NotificationMapper::toDomain);
  }

  @Override
  public void markAsRead(Notification notification) {
    notificationCustomRepository.updateIsReadByEventId(notification.getEventId(),
        notification.isRead());
  }

  @Override
  public void markAllAsRead(String receiverId) {
    notificationCustomRepository.updateAllIsReadByReceiverId(receiverId);
  }
}