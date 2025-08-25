package com.threadly.notification.adapter.kafka.notification;

import com.threadly.notification.core.port.notification.in.NotificationCommand;

public class NotificationMapper {

  /**
   * PostLikeEvent -> command
   *
   * @param notificationEvent
   * @return
   */
  public static NotificationCommand toCommand(NotificationEvent notificationEvent) {
    return new NotificationCommand(
        notificationEvent.getEventId(),
        notificationEvent.getReceiverUserId(),
        notificationEvent.getNotificationType(),
        notificationEvent.getMetadata(),
        notificationEvent.getOccurredAt()
    );
  }

}
