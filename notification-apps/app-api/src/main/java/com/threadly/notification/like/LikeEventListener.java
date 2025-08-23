package com.threadly.notification.like;

import com.threadly.notification.kafka.like.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventListener {

  @Async
  @EventListener
  public void handleLikeEvent(LikeEvent event) {
    log.info("Like event: {}", event.toString());
  }


}
