package com.threadly.notification.adapter.kafka.notification;

import com.threadly.notification.core.port.notification.in.NotificationCommandUseCase;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

  private final NotificationCommandUseCase notificationCommandUseCase;

  @Bean("notification")
  public Consumer<NotificationEvent> notification() {
    return event -> {
      notificationCommandUseCase.handleNotificationEvent(
          NotificationMapper.toCommand(event)
      );
    };
  }
}

