package com.threadly.notification.core.port.notification.out.dto.preview;

import com.threadly.notification.core.domain.notification.ActorProfile;

public record FollowPreview(ActorProfile actorProfile) implements Preview {

  @Override
  public String getTitle() {
    return actorProfile.getNickname();
  }

  @Override
  public String getBody() {
    return actorProfile.getNickname() + "님이 회원님을 팔로우합니다.";
  }

  @Override
  public String getImageUrl() {
    return actorProfile.getProfileImageUrl();
  }
}
