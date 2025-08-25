package com.threadly.notification.commons.exception.notification;


import com.threadly.notification.commons.exception.ErrorCode;

/**
 * 사용자 관련 예외
 */
public class NotificationException extends RuntimeException {

  ErrorCode errorCode;

  public NotificationException(ErrorCode errorCode) {
    super(errorCode.getDesc());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
