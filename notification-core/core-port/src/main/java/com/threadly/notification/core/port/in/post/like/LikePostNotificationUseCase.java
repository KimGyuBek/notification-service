package com.threadly.notification.core.port.in.post.like;

import com.threadly.notification.core.domain.post.LikeEventType;

/**
 * 게시글 좋아요 알림 usecase
 */
public interface LikePostNotificationUseCase {

  void handleLikeEvent(String userId, String postId, LikeEventType eventType);

}
