package com.threadly.notification.adapter.persistence.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@Document(collection = "notifications")
public class NotificationEntity {

  @Id
  private String id;

  @NotBlank
  @Field("user_id")
  private String userId;

  @NotBlank
  @Field("post_id") 
  private String postId;

  @NotNull
  @Field("notification_type")
  private String notificationType;

  @Field("content")
  private String content;

  @Field("is_read")
  private boolean isRead;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field("updated_at")
  private LocalDateTime updatedAt;

  // 기본 생성자 (MongoDB 필수)
  public NotificationEntity() {
    this.isRead = false;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public NotificationEntity(String id, String userId, String postId, String notificationType, 
                           String content, boolean isRead, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.postId = postId;
    this.notificationType = notificationType;
    this.content = content;
    this.isRead = isRead;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}