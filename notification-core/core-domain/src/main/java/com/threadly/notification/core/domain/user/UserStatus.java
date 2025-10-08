package com.threadly.notification.core.domain.user;

/**
 * 사용자 상태 enum
 */
public enum UserStatus {
  INCOMPLETE_PROFILE, //프로필 설정을 하지 않은 회원
  ACTIVE, //정상 회원
  INACTIVE, // 비활성화 상태
  DELETED, //탈퇴
  BANNED // 운영자에 의한 차단
}
