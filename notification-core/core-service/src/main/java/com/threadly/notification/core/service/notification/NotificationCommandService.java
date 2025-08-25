package com.threadly.notification.core.service.notification;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.notification.NotificationException;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.in.NotificationCommandUseCase;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import com.threadly.notification.core.service.utils.MetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommandUseCase {

  private final NotificationCommandPort notificationCommandPort;
  private final NotificationQueryPort notificationQueryPort;

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
        new ActorProfile(
            command.actorProfile().userId(),
            command.actorProfile().nickname(),
            command.actorProfile().profileImageUrl()
        ),
        metadataMapper.toTypeMeta(command.notificationType(), command.metadata())
    );

    notificationCommandPort.saveNotification(notification);

    /*알림*/
  }

  @Override
  public void deleteNotificationByEventIdAndUserId(String eventId, String userId) {
    /*eventId, receiverId에 해당하는 알림이 있는지 조회*/
    if(!notificationQueryPort.existsNotificationByEventIdAndReceiverId(eventId, userId)) {
      throw new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    /*삭제 처리*/
    notificationCommandPort.deleteNotificationByEventId(eventId);
  }
}
