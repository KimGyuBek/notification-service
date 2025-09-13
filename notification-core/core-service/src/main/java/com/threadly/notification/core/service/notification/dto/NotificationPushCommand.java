package com.threadly.notification.core.service.notification.dto;

import com.threadly.notification.core.domain.notification.Notification;

/**
 * Notification 발행 command 객채
 */
public record NotificationPushCommand(
    Notification notification,
    String sortId
) {

  public static NotificationPushCommand newCommand(Notification notification, String sortId) {
    return new NotificationPushCommand(
        notification,
        sortId
    );
  }
}
