package com.threadly.notification.core.domain.notification.metadata;

public record PostLikeMeta(
    String postId, String likerId
) implements NotificationMetaData {

}
