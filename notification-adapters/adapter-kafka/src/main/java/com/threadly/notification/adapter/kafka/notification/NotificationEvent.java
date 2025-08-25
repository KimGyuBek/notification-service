package com.threadly.notification.adapter.kafka.notification;

import com.threadly.notification.core.domain.notification.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
public class NotificationEvent {

  /*eventId*/
  private String eventId;

  /*알림 받는 사용자*/
  private String receiverUserId;

  /*Event 종류*/
  private NotificationType notificationType;

  /*이벤트 발생 시각*/
  private LocalDateTime occurredAt;

  /*메타 데이터*/
  private Map<String, Object> metadata;
}

/*
echo '{"eventId": "event1", "likerId": "user1", "receiverUserId" : "user2", "eventType" : "ADD", "occurredAt": "2021-11-08T12:42:11.769062"}' | kafka-console-producer --broker-list localhost:9092 --topic like

*/