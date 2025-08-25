package com.threadly.notification.core.port.notification.in.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.threadly.notification.core.domain.notification.Notification.ActorProfile;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import java.time.LocalDateTime;

/**
 * Notification 상세 조회 API 응답 객체
 */
public record GetNotificationDetailsApiResponse(
    String eventId,
    String receiverId,
    NotificationType notificationType,
    LocalDateTime occurredAt,
    ActorProfile actorProfile,
    boolean isRead,
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = PostLikeMeta.class, name = "POST_LIKE"),
        @JsonSubTypes.Type(value = PostCommentMeta.class, name = "POST_COMMENT"),
        @JsonSubTypes.Type(value = CommentLikeMeta.class, name = "COMMENT_LIKE"),
        @JsonSubTypes.Type(value = FollowRequestMeta.class, name = "FOLLOW_REQUEST")
    })
    NotificationMetaData metaData
) {

}
