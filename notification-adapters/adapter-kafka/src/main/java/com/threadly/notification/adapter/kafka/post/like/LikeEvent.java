package com.threadly.notification.adapter.kafka.post.like;

import com.threadly.notification.core.domain.post.LikeEventType;
import lombok.Data;

@Data
public class LikeEvent {

  private LikeEventType type;
  private String postId;
  private String userId;

}
