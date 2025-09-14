package com.threadly.notification.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenTestUtils {

  @Value("${jwt.secret}")
  private String secretKey;

  /**
   * accessToken 생성 (기본 1시간 만료)
   *
   * @param userId
   * @param userType
   * @param userStatusType
   * @return
   */
  public String generateAccessToken(String userId, String userType, String userStatusType) {
    return generateAccessToken(userId, userType, userStatusType, Duration.ofHours(1));
  }

  /**
   * accessToken 생성 (만료 시간 지정)
   *
   * @param userId
   * @param userType
   * @param userStatusType
   * @param duration 만료 시간
   * @return
   */
  public String generateAccessToken(String userId, String userType, String userStatusType, Duration duration) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("userType", userType)
            .claim("userStatusType", userStatusType)
            .setId(UUID.randomUUID().toString().substring(0, 8))
            .setIssuedAt(now)
            .setExpiration(
                Date.from(instant.plus(duration))
            )
            .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * 만료된 accessToken 생성
   *
   * @param userId
   * @param userType
   * @param userStatusType
   * @return
   */
  public String generateExpiredAccessToken(String userId, String userType, String userStatusType) {
    return generateAccessToken(userId, userType, userStatusType, Duration.ofSeconds(-1));
  }


  /**
   * SiginingKey 생성
   */
  private SecretKey generateSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
