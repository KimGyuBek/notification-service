package com.threadly.notification.auth;

import com.threadly.notification.commons.security.JwtTokenProvider;
import com.threadly.notification.core.domain.user.UserStatusType;
import com.threadly.notification.core.port.token.out.FetchTokenPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthManager {

  private final JwtTokenProvider jwtTokenProvider;

  private final FetchTokenPort fetchTokenPort;

  public Authentication getAuthentication(String accessToken) {
    /*token에서 claim 추출*/
    String userId = jwtTokenProvider.getUserId(accessToken);
    UserStatusType userStatusType = jwtTokenProvider.getUserStatusType(accessToken);

    /*권한 설정*/
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + userStatusType.name())
    );

    /*인증 객체 생성*/
    JwtAuthenticationUser authenticationUser = new JwtAuthenticationUser(
        userId,
        userStatusType,
        authorities
    );

    return new UsernamePasswordAuthenticationToken(
        authenticationUser,
        null,
        authenticationUser.getAuthorities()
    );
  }

  /**
   * blacklist 검증
   *
   * @param token
   * @return
   */
  public boolean isBlacklisted(String token) {
    return
        fetchTokenPort.existsBlackListTokenByAccessToken(token);
  }
}
