package com.threadly.notification.core.port.notification.out.dto.preview;

import com.threadly.notification.core.domain.user.ActorProfile;

public record FollowAcceptPreview(ActorProfile actorProfile) implements Preview {

  @Override
  public String getTitle() {
    return
        actorProfile.getNickname();
  }

  @Override
  public String getBody() {
    return actorProfile.getNickname() + "님이 팔로우를 수락했습니다.";
  }

  @Override
  public String getImageUrl() {
    return actorProfile.getProfileImageUrl();
  }
}
