package com.threadly.notification.commons.exception.token;


import com.threadly.notification.commons.exception.ErrorCode;

/**
 * Token 관련 예외
 */
public class TokenException extends RuntimeException {

  ErrorCode errorCode;

  public TokenException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
