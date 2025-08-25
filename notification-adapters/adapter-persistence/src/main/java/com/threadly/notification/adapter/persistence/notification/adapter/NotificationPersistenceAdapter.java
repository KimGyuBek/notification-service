package com.threadly.notification.adapter.persistence.notification.adapter;

import com.threadly.notification.adapter.persistence.notification.mapper.NotificationMapper;
import com.threadly.notification.adapter.persistence.notification.repository.MongoNotificationRepository;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPersistenceAdapter implements NotificationCommandPort,
    NotificationQueryPort {

  private final MongoNotificationRepository mongoNotificationRepository;

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
}