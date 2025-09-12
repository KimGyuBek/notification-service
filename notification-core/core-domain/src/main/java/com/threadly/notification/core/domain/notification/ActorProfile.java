package com.threadly.notification.core.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 행위자 도메인
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorProfile {
  private String userId;
  private String nickname;
  private String profileImageUrl;

}
