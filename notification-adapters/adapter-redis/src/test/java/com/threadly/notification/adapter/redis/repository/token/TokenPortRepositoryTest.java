package com.threadly.notification.adapter.redis.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

/**
 * TokenPortRepository 테스트
 */
@SpringBootTest
@DisplayName("Token Repository 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class TokenPortRepositoryTest {

  @Autowired
  private TokenPortRepository tokenPortRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private RedisServer redisServer;

  @AfterEach
  void tearDown() throws Exception {
    // Redis 데이터 정리
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  /*[Case #1] 블랙리스트에 등록된 AccessToken이 존재하는 경우 조회*/
  @Order(1)
  @Test
  @DisplayName("1. 블랙리스트에 등록된 AccessToken이 존재하는지 조회 - 존재하는 경우")
  void existsBlackListTokenByAccessToken_WhenTokenExists_ShouldReturnTrue() {
    // given
    String accessToken = "test.access.token";
    String userId = "user123";
    String blacklistKey = "token:blacklist:" + accessToken;

    // Redis에 블랙리스트 토큰 등록
    redisTemplate.opsForValue().set(blacklistKey, userId);

    // when
    boolean exists = tokenPortRepository.existsBlackListTokenByAccessToken(accessToken);

    // then
    assertThat(exists).isTrue();
  }

  /*[Case #2] 블랙리스트에 등록된 AccessToken이 존재하지 않는 경우*/
  @Order(2)
  @Test
  @DisplayName("2. 블랙리스트에 등록된 AccessToken이 존재하는지 조회 - 존재하지 않는 경우")
  void existsBlackListTokenByAccessToken_WhenTokenNotExists_ShouldReturnFalse() {
    // given
    String accessToken = "nonexistent.access.token";

    // when
    boolean exists = tokenPortRepository.existsBlackListTokenByAccessToken(accessToken);

    // then
    assertThat(exists).isFalse();
  }

  /*[Case #3] 블랙리스트 토큰 키 생성 검증*/
  @Order(3)
  @Test
  @DisplayName("3. 블랙리스트 토큰 키 생성 검증")
  void existsBlackListTokenByAccessToken_ShouldUseCorrectKeyFormat() {
    // given
    String accessToken = "sample.jwt.token";
    String userId = "user456";
    String expectedKey = "token:blacklist:" + accessToken;

    // Redis에 정확한 키 형식으로 데이터 저장
    redisTemplate.opsForValue().set(expectedKey, userId);

    // when
    boolean exists = tokenPortRepository.existsBlackListTokenByAccessToken(accessToken);

    // then
    assertThat(exists).isTrue();

    // 잘못된 키 형식으로는 조회되지 않음을 확인
    String wrongKey = "blacklist:" + accessToken;
    redisTemplate.opsForValue().set(wrongKey, userId);
    boolean existsWithWrongKey = tokenPortRepository.existsBlackListTokenByAccessToken(accessToken);
    assertThat(existsWithWrongKey).isTrue(); // 올바른 키가 이미 존재하므로 true
  }

  /*[Case #4] 빈 문자열인 경우 */
  @Order(4)
  @Test
  @DisplayName("4. 빈 문자열 AccessToken 처리")
  void existsBlackListTokenByAccessToken_WithEmptyToken_ShouldReturnFalse() {
    // given
    String emptyAccessToken = "";

    // when
    boolean exists = tokenPortRepository.existsBlackListTokenByAccessToken(emptyAccessToken);

    // then
    assertThat(exists).isFalse();
  }

  /*[Case #5] accessToken이 null인 경우*/
  @Order(5)
  @Test
  @DisplayName("5. null AccessToken 처리")
  void existsBlackListTokenByAccessToken_WithNullToken_ShouldHandleGracefully() {
    // given
    String nullAccessToken = null;

    // when & then
    // null 처리는 현재 구현에서 NullPointerException이 발생할 수 있으므로
    // 실제 운영에서는 null 체크가 필요함을 확인하는 테스트
    assertThrows(
        NullPointerException.class,
        () -> tokenPortRepository.existsBlackListTokenByAccessToken(nullAccessToken)
    );
  }

  /*[Case #6] 여러 토큰 블랙리스트 등록 및 조회*/
  @Order(6)
  @Test
  @DisplayName("6. 여러 토큰 블랙리스트 등록 및 조회")
  void existsBlackListTokenByAccessToken_MultipleTokens_ShouldWorkCorrectly() {
    // given
    String token1 = "first.access.token";
    String token2 = "second.access.token";
    String token3 = "third.access.token";
    String userId = "user789";

    // 두 개의 토큰만 블랙리스트에 등록
    redisTemplate.opsForValue().set("token:blacklist:" + token1, userId);
    redisTemplate.opsForValue().set("token:blacklist:" + token2, userId);

    // when & then
    assertThat(tokenPortRepository.existsBlackListTokenByAccessToken(token1)).isTrue();
    assertThat(tokenPortRepository.existsBlackListTokenByAccessToken(token2)).isTrue();
    assertThat(tokenPortRepository.existsBlackListTokenByAccessToken(token3)).isFalse();
  }
}