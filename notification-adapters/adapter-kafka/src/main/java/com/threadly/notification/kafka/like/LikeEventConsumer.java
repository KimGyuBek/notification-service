package com.threadly.notification.kafka.like;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventConsumer {

  private final ApplicationEventPublisher applicationEventPublisher;


  @Bean("like")
  public Consumer<LikeEvent> like() {
    return event -> {
//      log.info("Received like event: {}", event);
      applicationEventPublisher.publishEvent(event);
    };
  }
}

/*
echo '{"type": "ADD", "postId":"post1", "userId":"user1"}' | kafka-console-producer --broker-list localhost:9092 --topic like-in-0
* */
