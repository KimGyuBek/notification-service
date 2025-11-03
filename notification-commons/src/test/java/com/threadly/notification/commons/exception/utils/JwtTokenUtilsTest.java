package com.threadly.notification.commons.exception.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.token.TokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JwtTokenUtils 테스트
 */
class JwtTokenUtilsTest {

  @Nested
  @DisplayName("extractAccessToken 테스트")
  class ExtractAccessTokenTest {

    /*[Case #1] Bearer 접두사가 있는 경우 accessToken을 반환해야 한다*/
    @DisplayName("1. Bearer 접두사가 있는 경우 accessToken이 정상적으로 추출되는지 검증")
    @Test
    void extractAccessToken_shouldReturnToken_whenBearerPrefixPresent() throws Exception {
      //given
      String bearerToken = "Bearer sample.access.token";

      //when
      String accessToken = JwtTokenUtils.extractAccessToken(bearerToken);

      //then
      assertThat(accessToken).isEqualTo("sample.access.token");
    }

    /*[Case #2] null 이면 TOKEN_MISSING 예외가 발생해야 한다*/
    @DisplayName("2. null 값이면 TOKEN_MISSING 예외가 발생하는지 검증")
    @Test
    void extractAccessToken_shouldThrow_whenTokenIsNull() throws Exception {
      //given
      String bearerToken = null;

      //when & then
      assertThatThrownBy(() -> JwtTokenUtils.extractAccessToken(bearerToken))
          .isInstanceOf(TokenException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.TOKEN_MISSING);
    }

    /*[Case #3] Bearer 접두사가 없으면 TOKEN_MISSING 예외가 발생해야 한다*/
    @DisplayName("3. Bearer 접두사가 없으면 TOKEN_MISSING 예외가 발생하는지 검증")
    @Test
    void extractAccessToken_shouldThrow_whenPrefixMissing() throws Exception {
      //given
      String bearerToken = "Invalid token";

      //when & then
      assertThatThrownBy(() -> JwtTokenUtils.extractAccessToken(bearerToken))
          .isInstanceOf(TokenException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.TOKEN_MISSING);
    }
  }
}
