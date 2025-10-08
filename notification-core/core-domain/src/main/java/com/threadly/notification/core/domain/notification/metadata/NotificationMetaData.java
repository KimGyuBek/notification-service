package com.threadly.notification.core.domain.notification.metadata;

/**
 * Notification metadata
 */
public sealed interface NotificationMetaData
    permits PostLikeMeta, PostCommentMeta, CommentLikeMeta, FollowRequestMeta, FollowMeta,
    FollowAcceptMeta {
}

