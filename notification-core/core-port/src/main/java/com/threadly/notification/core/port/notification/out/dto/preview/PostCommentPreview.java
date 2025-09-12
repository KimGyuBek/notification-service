package com.threadly.notification.core.port.notification.out.dto.preview;

import com.threadly.notification.core.domain.notification.ActorProfile;
import com.threadly.notification.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;

public record PostCommentPreview(ActorProfile actorProfile,
                                 NotificationMetaData metadata) implements Preview {

  @Override
  public String getTitle() {
    return actorProfile.getNickname();
  }

  @Override
  public String getBody() {
    return "새 댓글:" +
        ((PostCommentMeta) metadata).commentExcerpt();
  }

  @Override
  public String getImageUrl() {
    return actorProfile.getProfileImageUrl();
  }
}
