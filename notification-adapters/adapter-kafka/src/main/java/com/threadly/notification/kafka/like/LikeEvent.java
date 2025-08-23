package com.threadly.notification.kafka.like;

import lombok.Data;

@Data
public class LikeEvent {

  private LikeEventType type;
  private String postId;
  private String userId;

}
