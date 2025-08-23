package com.threadly.notification.adapter.kafka.post.like;

import com.threadly.notification.core.domain.post.LikeEventType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PostLikeEvent {

  /*eventId*/
  private String eventId;

  /*좋아요 누른 사용자 id*/
  private String likerId;

  /*알림 받을 사용자 id*/
  private String receiverUserId;

  /*event type*/
  private LikeEventType type;

  /*이벤트 발생 시각*/
  private LocalDateTime occurredAt;

}

/*
echo '{"eventId": "event1", "likerId": "user1", "receiverUserId" : "user2", "type" : "ADD", "occurredAt": "2021-11-08T12:42:11.769062"}' | kafka-console-producer --broker-list localhost:9092 --topic like

*/