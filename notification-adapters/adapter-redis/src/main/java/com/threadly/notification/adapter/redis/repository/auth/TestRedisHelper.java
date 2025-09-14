package com.threadly.notification.adapter.redis.repository.auth;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 테스트시 사용하는 Redis helper
 */
@Profile("test")
@Repository
@RequiredArgsConstructor
public class TestRedisHelper {

  private final RedisTemplate<String, Object> redisTemplate;

  public void insertLoginAttempt(String userId, int attempts) {
    String key = "login:attempt:" + userId;

    redisTemplate.opsForValue().set(key, attempts);
  }

  public void clearRedis() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }


  public void insert(String key, String value, Duration duration) {
    redisTemplate.opsForValue().set(key, duration.toMillis());
  }

  /*
   * key : token:blacklist:{accessToken}
   * value : {userId}
   * */
  private static String generateBlackListKey(String accessToken) {
    return "token:blacklist:" + accessToken;
  }

}
