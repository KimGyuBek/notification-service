package com.threadly.notification.core.domain.notification.metadata;

public record PostCommentMeta(
    String postId, String commentId, String commenterId, String commentExcerpt
) implements NotificationMetaData{

}
