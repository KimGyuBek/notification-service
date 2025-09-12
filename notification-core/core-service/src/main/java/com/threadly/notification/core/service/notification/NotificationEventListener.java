package com.threadly.notification.core.service.notification;

import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private final NotificationDeliveryService notificationDeliveryService;

  @Async("eventExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onNotificationPublished(NotificationPushCommand command) {
    try {
      notificationDeliveryService.pushNotification(command);
      log.debug("알림 발행 요청 성공");

    } catch (Exception e) {
      log.error("알림 발행 요청 실패: eventId={}, error message={}", command.notification().getEventId(),
          e);
      throw e;
    }
  }

}
