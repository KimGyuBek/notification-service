package com.threadly.notification.adapter.websocket.interceptor;

import com.threadly.notification.commons.security.JwtTokenProvider;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    /*토큰 검증*/
    String token = getTokenFromRequest(request);
    if (jwtTokenProvider.validateToken(token)) {
      log.info("토큰 검증 성공");

      /*attribute에 userId 추가*/
      String userId = jwtTokenProvider.getUserId(token);
      attributes.put("userId", userId);

      return true;
    }
    log.warn("토큰 검증 실패");
    return false;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
    log.debug("Handshake 성공");
  }

  /**
   * 주어진 request에서 token 추출
   *
   * @param request
   * @return
   */
  private String getTokenFromRequest(ServerHttpRequest request) {
    URI uri = request.getURI();
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build()
        .getQueryParams();
    return
        queryParams.getFirst("token");
  }


}
