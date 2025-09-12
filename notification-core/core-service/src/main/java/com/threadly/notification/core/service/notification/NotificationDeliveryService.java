package com.threadly.notification.core.service.notification;

import com.threadly.notification.core.domain.notification.ActorProfile;
import com.threadly.notification.core.domain.notification.Notification;
import com.threadly.notification.core.port.notification.out.NotificationPushPort;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage;
import com.threadly.notification.core.port.notification.out.dto.NotificationMessage.Payload;
import com.threadly.notification.core.port.notification.out.dto.preview.CommentLikePreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowAcceptPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.FollowRequestPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.PostCommentPreview;
import com.threadly.notification.core.port.notification.out.dto.preview.PostLikePreview;
import com.threadly.notification.core.port.notification.out.dto.preview.Preview;
import com.threadly.notification.core.service.notification.dto.NotificationPushCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실시간 알림 발행 서비스
 */
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {

  private final NotificationPushPort notificationPushPort;

  /**
   * 주어진 userId에 해당하는 사용자에게 notification 발행
   *
   * @param command
   */
  @Transactional
  public void pushNotification(NotificationPushCommand command) {

    notificationPushPort.pushToUser(
        command.notification().getReceiverId(),
        new NotificationMessage(
            command.notification().getEventId(),
            command.sortId(),
            new Payload(
                command.notification().getNotificationType(),
                command.notification().getMetadata(),
                generatePayload(command.notification())
            ),
            command.notification().getOccurredAt()
        )
    );
  }

  private static Preview generatePayload(Notification notification) {
    ActorProfile actorProfile = notification.getActorProfile();

    return
        switch (notification.getNotificationType()) {
          case POST_LIKE -> new PostLikePreview(actorProfile);
          case COMMENT_ADDED -> new PostCommentPreview(actorProfile, notification.getMetadata());
          case COMMENT_LIKE -> new CommentLikePreview(actorProfile);
          case FOLLOW -> new FollowPreview(actorProfile);
          case FOLLOW_REQUEST -> new FollowRequestPreview(actorProfile);
          case FOLLOW_ACCEPT -> new FollowAcceptPreview(actorProfile);
        };
  }
}
