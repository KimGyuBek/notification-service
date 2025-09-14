//package com.threadly.notification.adapter.redis.repository.email;
//
//import com.threadly.commons.exception.ErrorCode;
//import com.threadly.commons.exception.mail.EmailVerificationException;
//import com.threadly.core.port.verification.EmailVerificationPort;
//import java.time.Duration;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
///**
// * email 인증 repository
// */
//@Repository
//@RequiredArgsConstructor
//@Slf4j
//public class EmailVerificationRepository implements EmailVerificationPort {
//
//  private final RedisTemplate<String, Object> redisTemplate;
//
//  /**
//   * code 저장
//   *
//   * @param userId
//   * @param code
//   */
//  public void saveCode(String userId, String code, Duration expiration) {
//    String key = generateKey(code);
//
//    log.debug("인증 코드 {}", code);
//
//    /**
//     * key : email:verify:{code}
//     * value : userId
//     */
//    redisTemplate.opsForValue().set(key, userId, expiration);
//    log.debug("인증 코드 저장 완료");
//  }
//
//  /**
//   * code로 userId 조회
//   *
//   * @param code
//   * @return
//   */
//  public String getUserId(String code) {
//    String key = generateKey(code);
//
//    String userId = (String) redisTemplate.opsForValue().get(key);
//
//    /*만료 되었거나 해당하는 사용자가 없는 경우*/
//    if (userId == null) {
//      throw new EmailVerificationException(ErrorCode.EMAIL_CODE_INVALID);
//    }
//
//    return userId;
//  }
//
//
//  /**
//   * code 삭제
//   *
//   * @param code
//   */
//  public void deleteCode(String code) {
//    String key = generateKey(code);
//
//    redisTemplate.delete(key);
//  }
//
//  /**
//   * key 생성
//   *
//   * @param code
//   * @return
//   */
//  private static String generateKey(String code) {
//    return "email:verify:" + code;
//  }
//
//
//}
