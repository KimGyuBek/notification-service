package com.threadly.notification.adapter.redis.repository.token;

import com.threadly.notification.core.port.token.out.FetchTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * token redis 저장 repository
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenPortRepository implements FetchTokenPort {

  private final RedisTemplate<String, String> redisTemplate;


  @Override
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
