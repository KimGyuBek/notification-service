package com.threadly.notification.core.port.notification.out.dto.preview;

import com.threadly.notification.core.domain.notification.ActorProfile;

public record PostLikePreview(
    ActorProfile actorProfile
) implements Preview {

  @Override
  public String getTitle() {
    return actorProfile.getNickname();
  }

  @Override
  public String getBody() {
    return actorProfile.getNickname() + "님이 회원님의 게시글을 좋아합니다.";
  }

  @Override
  public String getImageUrl() {
    return actorProfile.getProfileImageUrl();
  }
}
