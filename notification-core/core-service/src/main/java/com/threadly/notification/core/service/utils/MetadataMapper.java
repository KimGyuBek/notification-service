package com.threadly.notification.core.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetadataMapper {

  private final ObjectMapper objectMapper;

  /**
   * 주어진 type에 해당하는 metadata로 매핑
   *
   * @param type
   * @param raw
   * @return
   */
  public NotificationMetaData toTypeMeta(NotificationType type, Map<String, Object> raw) {
    Class<? extends NotificationMetaData> clazz = switch (type) {
      case POST_LIKE -> PostLikeMeta.class;
      case COMMENT_ADDED -> PostCommentMeta.class;
      case COMMENT_LIKE -> CommentLikeMeta.class;
      case FOLLOW_REQUEST -> FollowRequestMeta.class;
    };
    return objectMapper.convertValue(raw, clazz);
  }

}
