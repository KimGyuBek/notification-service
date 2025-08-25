package com.threadly.notification.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.response.ApiResponse;
import com.threadly.notification.global.exception.TokenAuthenticationException;
import com.threadly.notification.global.exception.UserAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * SpringSecurty Authentication 인증 오류 핸들링
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=utf-8");

    /*TokenAuthenticationException일 경우*/
    if (authException instanceof TokenAuthenticationException) {
      ErrorCode errorCode = ((TokenAuthenticationException) authException).getErrorCode();

      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      /*UserAuthenticationException일 경우*/
    } else if (authException instanceof UserAuthenticationException) {
      ErrorCode errorCode = ((UserAuthenticationException) authException).getErrorCode();
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));

      /*나머지 예외*/
    } else {
      ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
      response.setStatus(errorCode.getHttpStatus().value());
      objectMapper.writeValue(response.getWriter(),
          ApiResponse.fail(errorCode));
    }
  }
}
