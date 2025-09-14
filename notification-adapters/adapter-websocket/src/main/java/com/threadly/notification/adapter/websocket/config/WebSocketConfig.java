package com.threadly.notification.adapter.websocket.config;

import com.threadly.notification.adapter.websocket.interceptor.JwtHandshakeInterceptor;
import com.threadly.notification.adapter.websocket.notification.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final NotificationWebSocketHandler handler;
  private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws/notifications")
        .addInterceptors(jwtHandshakeInterceptor)
        .setAllowedOriginPatterns("*");
  }
}
