package com.threadly.notification.config;

import com.threadly.notification.global.filter.CustomAuthenticationEntryPoint;
import com.threadly.notification.global.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    disableDefaultSecurity(http);
    configureAuthorization(http);
    configureFilters(http);
    configureExceptionHandling(http);

    return http.build();
  }

  /**
   * 예외 핸들링 설정
   *
   * @param http
   * @throws Exception
   */
  private void configureExceptionHandling(HttpSecurity http) throws Exception {
    http.exceptionHandling(
        exception -> exception.authenticationEntryPoint(
            customAuthenticationEntryPoint
        ));
  }

  /**
   * 커스텀 필터 등록
   *
   * @param http
   */
  private void configureFilters(HttpSecurity http) {
    /*jwt authentication filter*/
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }

  /**
   * 인가 설정
   *
   * @param http
   * @throws Exception
   */
  private static void configureAuthorization(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        auth -> auth
            .requestMatchers(
                "/api/test/kafka",
                "/ws/**")
            .permitAll()
            .anyRequest().authenticated()
    );
  }

  /**
   * 디폴트 설정 비활성화
   *
   * @param http
   * @throws Exception
   */
  private static void disableDefaultSecurity(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
  }


}
