package com.threadly.notification.core.port.token.out;

/**
 * token 조회 port
 */
public interface FetchTokenPort {

  /**
   * 주어진 accessToken에 해당하는 blacklistToken이 저장되어 있는지 조회
   *
   * @param accessToken
   * @return
   */
  boolean existsBlackListTokenByAccessToken(String accessToken);

}
