package com.threadly.notification.core.service.post.like;

import com.threadly.notification.core.port.in.post.like.LikePostNotificationUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostLikeNotificationService implements LikePostNotificationUseCase {

  @Override
  public void handleLikeEvent(String userId, String postId,
      com.threadly.notification.core.domain.post.LikeEventType eventType) {
    log.info("userId: {}, postId:{}, type:{}", userId, postId, eventType);
  }
}
