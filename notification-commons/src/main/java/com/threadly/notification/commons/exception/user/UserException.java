package com.threadly.notification.commons.exception.user;


import com.threadly.notification.commons.exception.ErrorCode;

/**
 * 사용자 관련 예외
 */
public class UserException extends RuntimeException {

  ErrorCode errorCode;

  public UserException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
