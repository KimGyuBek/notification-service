package com.threadly.notification.core.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.core.domain.mail.MailType;
import com.threadly.notification.core.domain.mail.model.VerificationModel;
import com.threadly.notification.core.domain.mail.model.WelcomeModel;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
import java.util.Map;
import org.apache.catalina.loader.WebappLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * MailModelMapper 테스트
 */
class MailModelMapperTest {

  private final MailModelMapper mailModelMapper = new MailModelMapper(new ObjectMapper());

  @Nested
  @DisplayName("toTypeModel 테스트")
  class ToTypeModelTest {

    /*[Case #1] WELCOME 타입이면 WelcomeModel로 변환되어야 한다*/
    @DisplayName("1. WELCOME 타입이면 WelcomeModel로 변환되는지 검증")
    @Test
    void toTypeModel_shouldConvertToWelcomeModel_whenTypeIsWelcome() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          MailType.WELCOME,
          "welcome@threadly.io",
          Map.of(
              "userName", "홍길동",
              "loginUrl", "https://threadly.io/login"
          )
      );

      //when
      WelcomeModel model = mailModelMapper.toTypeModel(command);

      //then
      assertThat(model.userName()).isEqualTo("홍길동");
      assertThat(model instanceof WelcomeModel).isTrue();
    }

    /*[Case #2] VERIFICATION 타입이면 VerificationModel로 변환되어야 한다*/
    @DisplayName("2. VERIFICATION 타입이면 VerificationModel로 변환되는지 검증")
    @Test
    void toTypeModel_shouldConvertToVerificationModel_whenTypeIsVerification() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          MailType.VERIFICATION,
          "verify@threadly.io",
          Map.of(
              "userName", "홍길동",
              "verificationUrl", "https://threadly.io/verify?code=123456"
          )
      );

      //when
      VerificationModel model = mailModelMapper.toTypeModel(command);

      //then
      assertThat(model.userName()).isEqualTo("홍길동");
      assertThat(model instanceof VerificationModel).isTrue();
    }
  }
}
