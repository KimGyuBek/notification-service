package com.threadly.notification.core.domain.notification.metadata;

/**
 * 게시글 좋아요 메타 데이터
 *
 * @param postId
 */
public record PostLikeMeta(
    String postId
) implements NotificationMetaData {

}
