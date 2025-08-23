package com.threadly.notification.adapter.kafka.post.like;

import com.threadly.notification.core.port.in.post.like.LikePostNotificationUseCase;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventConsumer {

  private final LikePostNotificationUseCase likePostNotificationUseCase;

  @Bean("like")
  public Consumer<PostLikeEvent> like() {
    return event -> {
      likePostNotificationUseCase.handleLikeEvent(
          PostLikeCommandMapper.toCommand(event)
      );
    };
  }
}

