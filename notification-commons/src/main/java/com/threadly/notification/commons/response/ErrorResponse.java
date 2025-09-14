package com.threadly.notification.commons.response;

import com.threadly.notification.commons.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
  private final ErrorCode errorCode;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
