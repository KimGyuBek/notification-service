package com.threadly.notification.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.notification.BaseApiTest;
import com.threadly.notification.CommonResponse;
import com.threadly.notification.adapter.redis.repository.token.TokenRepository;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.utils.AccessTokenTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@DisplayName("Security API 인증 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SecurityApiTest extends BaseApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccessTokenTestUtils accessTokenTestUtils;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private static final String API_URL = "/api/test/authentication";
  private static final String VALID_USER_ID = "user123";
  private static final String USER_TYPE = "USER";
  private static final String USER_STATUS_TYPE = "ACTIVE";

  @Order(1)
  @Test
  @DisplayName("1. 올바른 AccessToken으로 요청 - 성공")
  void testValidAccessToken() throws Exception {
    // given
    String validAccessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when
    CommonResponse<Void> result = sendTestRequest(validAccessToken,
        status().isOk());

    // then
    assert result.isSuccess() == true;
  }

  @Order(2)
  @Test
  @DisplayName("2. AccessToken이 없는 경우 - 400 Unauthorized")
  void testNoAuthorizationHeader() throws Exception {
    // when & then
    CommonResponse<Void> result = sendTestRequest("", status().isBadRequest());

    validateFailResponse(result, ErrorCode.TOKEN_INVALID);
  }

  @Order(3)
  @Test
  @DisplayName("3. 잘못된 형식의 AccessToken - 401 Unauthorized")
  void testInvalidAccessTokenFormat() throws Exception {
    // given
    String invalidToken = "invalid.token.format";

    // when & then
    CommonResponse<Void> response = sendTestRequest(invalidToken,
        status().isBadRequest());
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(4)
  @Test
  @DisplayName("4. Bearer 없이 토큰만 전송하는 경우 - 400 BadRequest")
  void testTokenWithoutBearer() throws Exception {
    // given
    String validAccessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // when & then
    MvcResult result = mockMvc.perform(get(API_URL)
            .header("Authorization", validAccessToken))
        .andExpect(status().isBadRequest())
        .andReturn();

    assert result.getResponse().getStatus() == 400;
  }

  @Order(5)
  @Test
  @DisplayName("5. 만료된 AccessToken - 401 Unauthorized")
  void testExpiredAccessToken() throws Exception {
    // given
    String expiredAccessToken = accessTokenTestUtils.generateExpiredAccessToken(VALID_USER_ID,
        USER_TYPE, USER_STATUS_TYPE);

    // when & then
    CommonResponse<Void> response = sendTestRequest(expiredAccessToken, status().isUnauthorized());
    validateFailResponse(response, ErrorCode.TOKEN_EXPIRED);

  }

  @Order(6)
  @Test
  @DisplayName("6. 블랙리스트에 등록된 AccessToken - 400 BadRequest")
  void testBlacklistedAccessToken() throws Exception {
    // given
    String accessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);

    // Redis에 블랙리스트 토큰 등록
    String blacklistKey = "token:blacklist:" + accessToken;
    redisTemplate.opsForValue().set(blacklistKey, VALID_USER_ID);

    // when & then
    CommonResponse<Void> response = sendTestRequest(accessToken, status().isBadRequest());
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);

    // 블랙리스트에 등록되어 있는지 확인
    boolean isBlacklisted = tokenRepository.existsBlackListTokenByAccessToken(accessToken);
    assert isBlacklisted;
  }

  @Order(7)
  @Test
  @DisplayName("7. 잘못된 서명을 가진 AccessToken - 400 Bad Request")
  void testInvalidSignatureAccessToken() throws Exception {
    // given - 유효한 토큰에 임의로 문자 추가하여 서명을 무효화
    String validAccessToken = accessTokenTestUtils.generateAccessToken(VALID_USER_ID, USER_TYPE,
        USER_STATUS_TYPE);
    String invalidSignatureToken = validAccessToken + "invalid";

    // when & then
    CommonResponse<Void> response = sendTestRequest(invalidSignatureToken,
        status().isBadRequest());
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(8)
  @Test
  @DisplayName("8. 빈 문자열 AccessToken - 400 BadRequest")
  void testEmptyAccessToken() throws Exception {
    // given
    String emptyToken = "";

    // when & then
    CommonResponse<Void> response = sendTestRequest(emptyToken, status().isBadRequest());
    validateFailResponse(response, ErrorCode.TOKEN_INVALID);
  }

  @Order(9)
  @Test
  @DisplayName("9. 여러 번의 정상적인 요청 - 성공")
  void testMultipleValidRequests() throws Exception {
    // given
    String accessToken1 = accessTokenTestUtils.generateAccessToken("user1", USER_TYPE,
        USER_STATUS_TYPE);
    String accessToken2 = accessTokenTestUtils.generateAccessToken("user2", USER_TYPE,
        USER_STATUS_TYPE);
    String accessToken3 = accessTokenTestUtils.generateAccessToken("user3", USER_TYPE,
        USER_STATUS_TYPE);

    // when & then
    sendTestRequest(accessToken1, status().isOk());
    sendTestRequest(accessToken2, status().isOk());
    sendTestRequest(accessToken3, status().isOk());
  }
}