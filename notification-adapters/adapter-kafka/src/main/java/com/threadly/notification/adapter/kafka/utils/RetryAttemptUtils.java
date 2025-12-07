package com.threadly.notification.adapter.kafka.utils;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.messaging.Message;

public class RetryAttemptUtils {

  /**
   * attemptValue 추출
   * @param message
   * @return
   */
  public static int getAttemptValue(Message<?> message) {
    AtomicInteger attempt = message.getHeaders()
        .get(IntegrationMessageHeaderAccessor.DELIVERY_ATTEMPT, AtomicInteger.class);
    int attemptValue = attempt != null ? attempt.get() : 1;
    return attemptValue;
  }
}
