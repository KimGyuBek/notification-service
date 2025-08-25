package com.threadly.notification.commons.response;

import com.threadly.notification.commons.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ApiResponse<T>(
    boolean success,
    String code,
    String message,
    T data,
    LocalDateTime timestamp

) {

  public static final String CODE_SUCCEED = "SUCCESS";

  public static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(
        true,
        CODE_SUCCEED,
        SUCCESS_MESSAGE,
        data,
        LocalDateTime.now()
    );
  }

  public static <T> ApiResponse<T> success(T data, ErrorCode errorCode) {
    return new ApiResponse<>(
        true,
        errorCode.getCode(),
        errorCode.getDesc(),
        data,
        LocalDateTime.now()
    );
  }
  public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
    return new ApiResponse<>(
        false,
        errorCode.getCode(),
        errorCode.getDesc(),
        defaultData(),
        LocalDateTime.now()
    );
  }

  /*빈 객체 반환*/
  private static <T> T defaultData() {
    if (List.class.isAssignableFrom(Object.class)) {
      return (T) Collections.emptyList(); // 빈 리스트 []
    }
    return (T) Map.of();
  }


}

