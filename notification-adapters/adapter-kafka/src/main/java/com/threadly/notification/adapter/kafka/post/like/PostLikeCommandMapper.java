package com.threadly.notification.adapter.kafka.post.like;

import com.threadly.notification.core.port.post.like.in.PostLikeNotificationCommand;

public class PostLikeCommandMapper {

  /**
   * PostLikeEvent -> command
   *
   * @param postLikeEvent
   * @return
   */
  public static PostLikeNotificationCommand toCommand(PostLikeEvent postLikeEvent) {
    return new PostLikeNotificationCommand(
        postLikeEvent.getEventId(),
        postLikeEvent.getLikerId(),
        postLikeEvent.getReceiverUserId(),
        postLikeEvent.getType(),
        postLikeEvent.getOccurredAt()
    );
  }

}
