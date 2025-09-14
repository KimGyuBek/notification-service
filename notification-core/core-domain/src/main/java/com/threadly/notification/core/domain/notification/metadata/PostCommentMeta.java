package com.threadly.notification.core.domain.notification.metadata;

/**
 * 게시글 댓글 알림 메타 데이터
 * @param postId
 * @param commentId
 * @param commentExcerpt
 */
public record PostCommentMeta(
    String postId, String commentId, String commentExcerpt
) implements NotificationMetaData{

}
