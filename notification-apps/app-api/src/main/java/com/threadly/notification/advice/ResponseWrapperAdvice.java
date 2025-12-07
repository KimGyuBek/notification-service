package com.threadly.notification.advice;

import com.threadly.notification.commons.response.ApiResponse;
import com.threadly.notification.commons.response.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice()
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {

    return org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.class
        .isAssignableFrom(converterType);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    String path = request.getURI().getPath();

    /*경로 제외*/
    if (path.startsWith("/actuator") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.startsWith("/v3-notification") ||
        path.equals("/swagger-ui.html")) {
      return body;
    }

    /*Json타입이 아니면 body 그대로 리턴*/
    if (selectedContentType == null) {
      return body;
    }
    boolean isJson = selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON) ||
        (selectedContentType.getSubtype() != null
            && selectedContentType.getSubtype().endsWith("+json"));
    if (!isJson) {
      return body;
    }

    /*file, binary, streaming, sse 스킵*/
    if (body instanceof byte[]
        || body instanceof org.springframework.core.io.Resource
        || body instanceof org.springframework.web.servlet.mvc.method.annotation.SseEmitter
        || body instanceof org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody) {
      return body;
    }

    if (body instanceof ErrorResponse) {
      return ApiResponse.fail(((ErrorResponse) body).getErrorCode());
    }

    return ApiResponse.success(body);
  }
}
