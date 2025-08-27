package com.threadly.notification.core.domain.notification.metadata;

/**
 * 게시글 댓글 좋아요 메타 데이터
 * @param postId
 * @param commentId
 * @param commentExcerpt
 */
public record CommentLikeMeta(
    String postId, String commentId,  String commentExcerpt
) implements NotificationMetaData {

}
