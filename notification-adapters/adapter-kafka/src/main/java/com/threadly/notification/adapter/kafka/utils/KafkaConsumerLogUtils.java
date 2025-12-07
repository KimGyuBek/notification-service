package com.threadly.notification.adapter.kafka.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;

/**
 * Kafka Consumer 로깅 Utils
 */
@Slf4j
public class KafkaConsumerLogUtils {

  /**
   * 재시도 로깅
   *
   * @param topic
   * @param attempt
   * @param eventId
   */
  public static void logRetry(String topic, int attempt, String eventId) {
    log.warn("[{}] 재시도 처리 중({}): eventId={}", topic, attempt, eventId);
  }

  /**
   * 실패 로깅
   */
  public static void logFailure(String topic, String eventId, Throwable ex) {
    log.warn("[{}] 처리 실패(재시도 예정): eventId={}", topic, eventId, ex);
  }

  /**
   * 성공 로깅
   *
   * @param topic
   * @param eventId
   */
  public static void logSuccess(String topic, String eventId) {
    log.info("[{}] 처리 성공: eventId={}", topic, eventId);
  }

  /**
   * 최종 실패 로깅
   *
   * @param errorMessage
   */
  public static void logFinalFailure(ErrorMessage errorMessage) {

    MessagingException ex = (MessagingException) errorMessage.getPayload();
    Message<?> failedMessage = ex.getFailedMessage();

    Object topic = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC);
    Object partition = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION);
    Object offset = failedMessage.getHeaders().get(KafkaHeaders.OFFSET);
    Object key = failedMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY);
    Object payload = failedMessage.getPayload();

    log.error(
        "최종 실패: topic={}, partition={}, offset={}, key={}, payload={}",
         topic, partition, offset, key, payload, ex
    );
  }

}
