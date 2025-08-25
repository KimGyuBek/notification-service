package com.threadly.notification.core.service.notification;

import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.service.utils.MetadataMapper;
import com.threadly.notification.core.port.post.like.in.NotificationCommandUseCase;
import com.threadly.notification.core.port.post.like.in.NotificationCommand;
import com.threadly.notification.core.port.post.like.out.NotificationCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommandUseCase {

  private final NotificationCommandPort notificationCommandPort;

  private final MetadataMapper metadataMapper;

  @Override
  public void handleNotificationEvent(NotificationCommand command) {
    log.info("Handling notification event: {}", command.toString());
    /*알림 저장*/
    Notification notification = Notification.newNotification(
        command.eventId(),
        command.receiverId(),
        command.notificationType(),
        command.occurredAt(),
        metadataMapper.toTypeMeta(command.notificationType(), command.metadata())
    );

    notificationCommandPort.saveNotification(notification);

    /*알림*/
  }
}
