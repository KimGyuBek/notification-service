package com.threadly.notification.core.port.notification.out.dto.preview;

public sealed interface Preview
permits PostLikePreview, PostCommentPreview , CommentLikePreview, FollowPreview, FollowRequestPreview, FollowAcceptPreview{
  String getTitle();
  String getBody();
  String getImageUrl();


}
