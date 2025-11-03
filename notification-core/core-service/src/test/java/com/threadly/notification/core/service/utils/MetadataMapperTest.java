package com.threadly.notification.core.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.core.domain.notification.NotificationType;
import com.threadly.notification.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowAcceptMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowMeta;
import com.threadly.notification.core.domain.notification.metadata.FollowRequestMeta;
import com.threadly.notification.core.domain.notification.metadata.PostCommentMeta;
import com.threadly.notification.core.domain.notification.metadata.PostLikeMeta;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * MetadataMapper 테스트
 */
class MetadataMapperTest {

  private final MetadataMapper metadataMapper = new MetadataMapper(new ObjectMapper());

  @Nested
  @DisplayName("toTypeMeta 테스트")
  class ToTypeMetaTest {

    /*[Case #1] POST_LIKE 타입이면 PostLikeMeta로 변환되어야 한다*/
    @DisplayName("1. POST_LIKE 타입이면 PostLikeMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToPostLikeMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of("postId", "post-1");

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.POST_LIKE, raw);

      //then
      assertThat(result).isInstanceOf(PostLikeMeta.class);
      assertThat(((PostLikeMeta) result).postId()).isEqualTo("post-1");
    }

    /*[Case #2] COMMENT_ADDED 타입이면 PostCommentMeta로 변환되어야 한다*/
    @DisplayName("2. COMMENT_ADDED 타입이면 PostCommentMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToPostCommentMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of(
          "postId", "post-1",
          "commentId", "comment-1",
          "commentExcerpt", "내용"
      );

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.COMMENT_ADDED, raw);

      //then
      assertThat(result).isInstanceOf(PostCommentMeta.class);
      PostCommentMeta meta = (PostCommentMeta) result;
      assertThat(meta.postId()).isEqualTo("post-1");
      assertThat(meta.commentId()).isEqualTo("comment-1");
      assertThat(meta.commentExcerpt()).isEqualTo("내용");
    }

    /*[Case #3] COMMENT_LIKE 타입이면 CommentLikeMeta로 변환되어야 한다*/
    @DisplayName("3. COMMENT_LIKE 타입이면 CommentLikeMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToCommentLikeMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of(
          "postId", "post-1",
          "commentId", "comment-1",
          "commentExcerpt", "내용"
      );

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.COMMENT_LIKE, raw);

      //then
      assertThat(result).isInstanceOf(CommentLikeMeta.class);
      CommentLikeMeta meta = (CommentLikeMeta) result;
      assertThat(meta.postId()).isEqualTo("post-1");
    }

    /*[Case #4] FOLLOW_REQUEST 타입이면 FollowRequestMeta로 변환되어야 한다*/
    @DisplayName("4. FOLLOW_REQUEST 타입이면 FollowRequestMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToFollowRequestMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of();

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.FOLLOW_REQUEST, raw);

      //then
      assertThat(result).isInstanceOf(FollowRequestMeta.class);
    }

    /*[Case #5] FOLLOW_ACCEPT 타입이면 FollowAcceptMeta로 변환되어야 한다*/
    @DisplayName("5. FOLLOW_ACCEPT 타입이면 FollowAcceptMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToFollowAcceptMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of();

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.FOLLOW_ACCEPT, raw);

      //then
      assertThat(result).isInstanceOf(FollowAcceptMeta.class);
    }

    /*[Case #6] FOLLOW 타입이면 FollowMeta로 변환되어야 한다*/
    @DisplayName("6. FOLLOW 타입이면 FollowMeta로 변환되는지 검증")
    @Test
    void toTypeMeta_shouldMapToFollowMeta() throws Exception {
      //given
      Map<String, Object> raw = Map.of();

      //when
      Object result = metadataMapper.toTypeMeta(NotificationType.FOLLOW, raw);

      //then
      assertThat(result).isInstanceOf(FollowMeta.class);
    }
  }
}
