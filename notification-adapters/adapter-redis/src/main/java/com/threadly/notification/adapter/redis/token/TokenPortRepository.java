package com.threadly.notification.adapter.redis.token;

import com.threadly.notification.core.port.token.out.FetchTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * token redis 저장 repository
 */

@Repository
@RequiredArgsConstructor
public class TokenPortRepository implements FetchTokenPort {

  private final TokenRepository tokenRepository;

  @Override
  public boolean existsBlackListTokenByAccessToken(String accessToken) {
    return
        tokenRepository.existsBlackListTokenByAccessToken(accessToken);
  }
}
