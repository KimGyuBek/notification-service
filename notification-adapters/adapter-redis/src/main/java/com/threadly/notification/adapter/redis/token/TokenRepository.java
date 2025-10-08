package com.threadly.notification.adapter.redis.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * TokenRepository
 */
@Repository
@RequiredArgsConstructor
public class TokenRepository {

  private final RedisTemplate<String, String> redisTemplate;


  public boolean existsBlackListTokenByAccessToken(String accessToken) {
    if (accessToken == null) {
      throw new NullPointerException("Access token is null");
    }
    String key = generateBlackListKey(accessToken);
    return
        redisTemplate.hasKey(key);
  }

  /*
   * key : token:blacklist:{accessToken}
   * value : {userId}
   * */
  private static String generateBlackListKey(String accessToken) {
    return "token:blacklist:" + accessToken;
  }



}
