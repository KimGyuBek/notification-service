package com.threadly.notification.commons.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.token.TokenException;
import com.threadly.notification.core.domain.user.UserStatusType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * JwtTokenProvider 테스트
 */
class JwtTokenProviderTest {

  private static final String RAW_SECRET =
      "notification-service-test-secret-key-256-bits";
  private static final String SECRET_KEY =
      Encoders.BASE64.encode(RAW_SECRET.getBytes(StandardCharsets.UTF_8));

  private JwtTokenProvider jwtTokenProvider;
  private SecretKey signingKey;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", SECRET_KEY);
    signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
  }

  private String createToken(Instant issuedAt, Instant expiresAt) {
    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .claim("userId", "user-1")
        .claim("userType", "USER")
        .claim("userStatusType", UserStatusType.ACTIVE.name())
        .setIssuedAt(Date.from(issuedAt))
        .setExpiration(Date.from(expiresAt))
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  @Nested
  @DisplayName("validateToken 테스트")
  class ValidateTokenTest {

    /*[Case #1] 유효한 토큰이면 true가 반환되어야 한다*/
    @DisplayName("1. 유효한 토큰이면 true가 반환되는지 검증")
    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() throws Exception {
      //given
      String token = createToken(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

      //when
      boolean result = jwtTokenProvider.validateToken(token);

      //then
      assertThat(result).isTrue();
    }

    /*[Case #2] 만료된 토큰이면 TOKEN_EXPIRED 예외가 발생해야 한다*/
    @DisplayName("2. 만료된 토큰이면 TOKEN_EXPIRED 예외가 발생하는지 검증")
    @Test
    void validateToken_shouldThrow_whenTokenIsExpired() throws Exception {
      //given
      String token = createToken(Instant.now().minus(2, ChronoUnit.HOURS),
          Instant.now().minus(1, ChronoUnit.HOURS));

      //when & then
      assertThatThrownBy(() -> jwtTokenProvider.validateToken(token))
          .isInstanceOf(TokenException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    /*[Case #3] 잘못된 토큰이면 TOKEN_INVALID 예외가 발생해야 한다*/
    @DisplayName("3. 잘못된 토큰이면 TOKEN_INVALID 예외가 발생하는지 검증")
    @Test
    void validateToken_shouldThrow_whenTokenIsInvalid() throws Exception {
      //given
      String invalidToken = "invalid.token.value";

      //when & then
      assertThatThrownBy(() -> jwtTokenProvider.validateToken(invalidToken))
          .isInstanceOf(TokenException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.TOKEN_INVALID);
    }
  }

  @Nested
  @DisplayName("토큰 Claim 추출 테스트")
  class ExtractClaimsTest {

    /*[Case #1] userId를 올바르게 추출해야 한다*/
    @DisplayName("1. 유효한 토큰에서 userId가 올바르게 추출되는지 검증")
    @Test
    void getUserId_shouldReturnValue_whenTokenIsValid() throws Exception {
      //given
      String token = createToken(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

      //when
      String userId = jwtTokenProvider.getUserId(token);

      //then
      assertThat(userId).isEqualTo("user-1");
    }

    /*[Case #2] userType을 올바르게 추출해야 한다*/
    @DisplayName("2. 유효한 토큰에서 userType이 올바르게 추출되는지 검증")
    @Test
    void getUserType_shouldReturnValue_whenTokenIsValid() throws Exception {
      //given
      String token = createToken(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

      //when
      String userType = jwtTokenProvider.getUserType(token);

      //then
      assertThat(userType).isEqualTo("USER");
    }

    /*[Case #3] userStatusType을 올바르게 추출해야 한다*/
    @DisplayName("3. 유효한 토큰에서 userStatusType이 올바르게 추출되는지 검증")
    @Test
    void getUserStatusType_shouldReturnEnum_whenTokenIsValid() throws Exception {
      //given
      String token = createToken(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

      //when
      UserStatusType statusType = jwtTokenProvider.getUserStatusType(token);

      //then
      assertThat(statusType).isEqualTo(UserStatusType.ACTIVE);
    }
  }
}
