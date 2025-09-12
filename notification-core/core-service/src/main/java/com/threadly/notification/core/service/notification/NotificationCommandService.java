package com.threadly.notification.core.service.notification;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.notification.NotificationException;
import com.threadly.notification.core.domain.notification.ActorProfile;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.in.NotificationCommand;
import com.threadly.notification.core.port.notification.in.NotificationCommandUseCase;
import com.threadly.notification.core.port.notification.in.NotificationIngestionUseCase;
import com.threadly.notification.core.port.notification.out.NotificationCommandPort;
import com.threadly.notification.core.port.notification.out.NotificationQueryPort;
import com.threadly.notification.core.port.notification.out.dto.SavedNotificationEventDoc;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import com.threadly.notification.core.service.utils.MetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommandUseCase,
    NotificationIngestionUseCase {

  private final NotificationCommandPort notificationCommandPort;
  private final NotificationQueryPort notificationQueryPort;

  private final MetadataMapper metadataMapper;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public void ingest(NotificationCommand command) {
    log.info("Handling notification event: {}", command.toString());
    /*도메인 생성*/
    Notification notification = Notification.newNotification(
        command.eventId(),
        command.receiverId(),
        command.notificationType(),
        command.occurredAt(),
        new ActorProfile(
            command.actorProfile().getUserId(),
            command.actorProfile().getNickname(),
            command.actorProfile().getProfileImageUrl()
        ),
        metadataMapper.toTypeMeta(command.notificationType(), command.metadata())
    );

    /*알림 저장*/
    SavedNotificationEventDoc saved = notificationCommandPort.save(notification);

    /*알림 발행*/
    applicationEventPublisher.publishEvent(
        new NotificationPushCommand(notification, saved.sortId())
    );

  }

  @Transactional
  @Override
  public void removeNotification(String eventId, String userId) {
    /*eventId, receiverId에 해당하는 알림이 있는지 조회*/
    if (!notificationQueryPort.existsByEventIdAndReceiverId(eventId, userId)) {
      throw new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    /*삭제 처리*/
    notificationCommandPort.deleteByEventId(eventId);
  }

  @Transactional
  @Override
  public void clearNotifications(String userId) {
    notificationCommandPort.deleteAllByReceiverId(userId);
  }

  @Transactional
  @Override
  public void markNotificationAsRead(String eventId, String userId) {

    /*notification 조회*/
    Notification notification = notificationQueryPort.fetchByEventIdAndReceiverId(eventId, userId)
        .orElseThrow(() ->
            new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

    /*읽음 처리*/
    notification.markAsRead();

    /*업데이트*/
    notificationCommandPort.markAsRead(notification);
  }

  @Transactional
  @Override
  public void markAllNotificationsAsRead(String userId) {
    notificationCommandPort.markAllAsRead(userId);
  }
}
