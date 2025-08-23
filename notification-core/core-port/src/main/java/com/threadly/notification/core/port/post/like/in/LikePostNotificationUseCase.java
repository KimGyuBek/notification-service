package com.threadly.notification.core.port.post.like.in;

/**
 * 게시글 좋아요 알림 usecase
 */
public interface LikePostNotificationUseCase {

  void handleLikeEvent(PostLikeNotificationCommand command);

}
