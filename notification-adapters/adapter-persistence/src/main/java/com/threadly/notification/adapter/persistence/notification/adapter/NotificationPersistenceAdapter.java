package com.threadly.notification.adapter.persistence.notification.adapter;

import com.threadly.notification.adapter.persistence.notification.mapper.NotificationMapper;
import com.threadly.notification.adapter.persistence.notification.repository.MongoNotificationRepository;
import com.threadly.notification.adapter.persistence.notification.repository.NotificationCursorRepository;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.in.dto.GetNotificationsQuery;
import com.threadly.notification.core.port.notification.in.dto.NotificationDetails;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceAdapter implements NotificationCommandPort,
    NotificationQueryPort {

  private final MongoNotificationRepository mongoNotificationRepository;
  private final NotificationCursorRepository notificationCursorRepository;

  @Override
  public void saveNotification(Notification notification) {
    mongoNotificationRepository.save(NotificationMapper.toEntity(notification));
  }

  @Override
  public Optional<Notification> fetchNotificationDetails(String eventId) {
    return
        mongoNotificationRepository.findById(eventId).map(
            NotificationMapper::toDomain
        );
  }

  @Override
  public List<NotificationDetails> fetchNotificationsByCursor(GetNotificationsQuery query) {
    return notificationCursorRepository.findNotificationsByCursor(query).stream().map(
        entity -> new NotificationDetails(
            entity.getEventId(),
            entity.getReceiverId(),
            entity.getNotificationType(),
            entity.getOccurredAt(),
            entity.getActorProfile()
        )
    ).collect(Collectors.toList());
  }
}