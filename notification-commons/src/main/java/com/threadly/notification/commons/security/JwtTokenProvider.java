package com.threadly.notification.commons.security;


import com.threadly.notification.core.domain.user.UserStatusType;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.token.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

//  private final TtlProperties ttlProperties;

  @Value("${jwt.secret}")
  private String secretKey;

  /**
   * header에서 jwt 추출
   *
   * @param request
   * @return
   */
  public String resolveToken(HttpServletRequest request) {
    /*authorization header 가져오기*/
    String bearerToken = request.getHeader("Authorization");

    /*bearer Token이 존재할 경우*/
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    /*존재하지 않을 경우*/
    throw new TokenException(ErrorCode.TOKEN_MISSING);
  }


  /**
   * validate Token
   *
   * @param token
   * @return
   */
  /*TODO 분리*/
  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(generateSigningKey())
          .build()
          .parseClaimsJws(token);

      log.info("토큰 검증됨");

      return true;

      /*토큰 만료*/
    } catch (ExpiredJwtException e) {
      log.warn("토큰 만료됨");
      throw new TokenException(ErrorCode.TOKEN_EXPIRED);

      /*기타 예외*/
    } catch (Exception e) {
      log.warn("토큰 검증 안 됨");
      throw new TokenException(ErrorCode.TOKEN_INVALID);
    }

  }

  /**
   * jwt에서 claim:userId 추출
   *
   * @param token
   * @return
   */
  public String getUserId(String token) {
    return
        getClaims(token).get("userId", String.class);
  }

  /**
   * jwt에서 claim:userType 추출
   *
   * @param token
   * @return
   */
  public String getUserType(String token) {
    return getClaims(token).get("userType", String.class);
  }

  /**
   * jwt에서 claim:userStatusType 추출
   *
   * @param token
   * @return
   */
  public UserStatusType getUserStatusType(String token) {
    return
        UserStatusType.valueOf(
            getClaims(token).get("userStatusType", String.class)
        );
  }


  private Claims getClaims(String token) {
    Claims body = Jwts.parserBuilder()
        .setSigningKey(generateSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return body;
  }

  /**
   * SiginingKey 생성
   */
  private SecretKey generateSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }


}
