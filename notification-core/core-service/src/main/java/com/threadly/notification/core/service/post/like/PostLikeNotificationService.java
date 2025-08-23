package com.threadly.notification.core.service.post.like;

import com.threadly.notification.core.port.post.like.in.LikePostNotificationUseCase;
import com.threadly.notification.core.port.post.like.in.PostLikeNotificationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostLikeNotificationService implements LikePostNotificationUseCase {

  @Override
  public void handleLikeEvent(PostLikeNotificationCommand command) {
    log.info("Handling like event: {}", command.toString());
    /*알림 저장*/

    /*알림*/
  }
}
