package com.threadly.notification.global.exception;

import com.threadly.notification.commons.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;

/**
 * Token 인증 관련 예외
 */
public class TokenAuthenticationException extends AuthenticationException {

  ErrorCode errorCode;

  public TokenAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
