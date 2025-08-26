package com.threadly.notification.controller;

import com.threadly.notification.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.notification.core.port.test.dto.NotificationTestCommand;
import com.threadly.notification.core.port.test.in.KafkaTestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트 controller
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

  private final KafkaTestUseCase kafkaTestUseCase;

  /**
   * authentication test
   *
   * @return
   */
  @GetMapping("/authentication")
  public ResponseEntity<Void> authenticationTest() {
    return ResponseEntity.ok().build();
  }

  @PostMapping("/kafka")
  public ResponseEntity<Void> kafkaTest(
      @RequestBody NotificationEvent notificationEvent
  ) {

    kafkaTestUseCase.sendNotificationEvent(
        new NotificationTestCommand(
            notificationEvent.getEventId(),
            notificationEvent.getReceiverUserId(),
            notificationEvent.getNotificationType(),
            notificationEvent.getActorProfile(),
            notificationEvent.getOccurredAt(),
            notificationEvent.getMetadata()
        )
    );
    return ResponseEntity.ok().build();
  }
}

