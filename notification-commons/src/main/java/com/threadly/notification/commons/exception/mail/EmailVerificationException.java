package com.threadly.notification.commons.exception.mail;


import com.threadly.notification.commons.exception.ErrorCode;

/**
 * 메일 전송 관련 예외
 */
public class EmailVerificationException extends RuntimeException {

  private ErrorCode errorCode;

  public EmailVerificationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
