package com.threadly.notification.adapter.persistence.notification.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "notifications")
public class NotificationEntity {

  @Id
  @Field("event_id")
  private String eventId;

  @NotBlank
  @Field("receiver_id")
  private String receiverId;

  @NotBlank
  @Field("notification_type")
  private NotificationType notificationType;

  @NotBlank
  @Field("is_read")
  private boolean isRead;

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = As.PROPERTY,
      property = "type"
  )
  @JsonSubTypes({
      @JsonSubTypes.Type(value = PostLikeMeta.class, name = "POST_LIKE"),
      @JsonSubTypes.Type(value = PostCommentMeta.class, name = "COMMENT_ADDED"),
      @JsonSubTypes.Type(value = CommentLikeMeta.class, name = "COMMENT_LIKE"),
      @JsonSubTypes.Type(value = FollowRequestMeta.class, name = "FOLLOW_REQUEST")
  })
  private NotificationMetaData metadata;

  @NotBlank
  @Field("occurred_at")
  private LocalDateTime occurredAt;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field("modified_at")
  private LocalDateTime modifiedAt;


  public NotificationEntity(String eventId, String receiverId, NotificationType notificationType,
      NotificationMetaData metadata, LocalDateTime occurredAt
  ) {
    this.eventId = eventId;
    this.receiverId = receiverId;
    this.notificationType = notificationType;
    this.occurredAt = occurredAt;
    this.isRead = false;
    this.metadata = metadata;
  }
}