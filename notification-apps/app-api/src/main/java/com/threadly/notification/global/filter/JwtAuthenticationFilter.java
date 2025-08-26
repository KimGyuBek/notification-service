package com.threadly.notification.global.filter;

import com.threadly.notification.auth.AuthManager;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.token.TokenException;
import com.threadly.notification.commons.exception.user.UserException;
import com.threadly.notification.commons.exception.utils.JwtTokenUtils;
import com.threadly.notification.commons.security.JwtTokenProvider;
import com.threadly.notification.global.exception.TokenAuthenticationException;
import com.threadly.notification.global.exception.UserAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Jwt 인증 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  private final AuthManager authManager;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return
        FilterBypassMatcher.shouldBypass(request.getRequestURI());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = JwtTokenUtils.extractAccessToken(request.getHeader("Authorization"));

      /*blacklist token 조회 후 있을경우 예외 처리*/
      if (authManager.isBlacklisted(token)) {
        throw new TokenException(ErrorCode.TOKEN_INVALID);
      }

      /*토큰이 검증되면*/
      if (jwtTokenProvider.validateToken(token)) {
        Authentication authentication = authManager.getAuthentication(token);

        /*인증*/
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      /*사용자 관련 오류 */
    } catch (UserException e) {
      UserAuthenticationException exception = new UserAuthenticationException(e.getErrorCode());
      authenticationEntryPoint.commence(request, response, exception);
      return;
    } catch (TokenException e) {
      TokenAuthenticationException exception = new TokenAuthenticationException(e.getErrorCode());
      authenticationEntryPoint.commence(request, response, exception);
      return;
    }

    filterChain.doFilter(request, response);
  }

}
