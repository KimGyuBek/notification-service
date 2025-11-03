package com.threadly.notification.commons.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.threadly.notification.commons.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ApiResponse 테스트
 */
class ApiResponseTest {

  @Nested
  @DisplayName("success 팩토리 메서드 테스트")
  class SuccessFactoryMethodTest {

    /*[Case #1] 기본 success 응답이 올바르게 생성되어야 한다*/
    @DisplayName("1. 기본 success 응답이 올바르게 생성되는지 검증")
    @Test
    void success_shouldCreateResponse_whenDataProvided() throws Exception {
      //given
      String data = "payload";

      //when
      ApiResponse<String> response = ApiResponse.success(data);

      //then
      assertThat(response.success()).isTrue();
      assertThat(response.code()).isEqualTo(ApiResponse.CODE_SUCCEED);
      assertThat(response.message()).isEqualTo(ApiResponse.SUCCESS_MESSAGE);
      assertThat(response.data()).isEqualTo(data);
      assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    /*[Case #2] ErrorCode 기반 success 응답이 올바르게 생성되어야 한다*/
    @DisplayName("2. ErrorCode 기반 success 응답이 올바르게 생성되는지 검증")
    @Test
    void success_shouldUseErrorCode_whenProvided() throws Exception {
      //given
      Map<String, String> data = Map.of("key", "value");

      //when
      ApiResponse<Map<String, String>> response = ApiResponse.success(data, ErrorCode.ACCESS_DENIED);

      //then
      assertThat(response.success()).isTrue();
      assertThat(response.code()).isEqualTo(ErrorCode.ACCESS_DENIED.getCode());
      assertThat(response.message()).isEqualTo(ErrorCode.ACCESS_DENIED.getDesc());
      assertThat(response.data()).isEqualTo(data);
      assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
  }

  @Nested
  @DisplayName("fail 팩토리 메서드 테스트")
  class FailFactoryMethodTest {

    /*[Case #1] 실패 응답이 올바르게 생성되어야 한다*/
    @DisplayName("1. 실패 응답이 올바르게 생성되는지 검증")
    @Test
    void fail_shouldCreateResponse_whenErrorCodeProvided() throws Exception {
      //given
      ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

      //when
      ApiResponse<Object> response = ApiResponse.fail(errorCode);

      //then
      assertThat(response.success()).isFalse();
      assertThat(response.code()).isEqualTo(errorCode.getCode());
      assertThat(response.message()).isEqualTo(errorCode.getDesc());
      assertThat(response.data()).isInstanceOf(Map.class);
      assertThat(((Map<?, ?>) response.data())).isEmpty();
      assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
  }
}
