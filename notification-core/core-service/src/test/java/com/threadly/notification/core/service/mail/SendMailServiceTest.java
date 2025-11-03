package com.threadly.notification.core.service.mail;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
import com.threadly.notification.core.domain.mail.MailType;
import com.threadly.notification.core.domain.mail.model.VerificationModel;
import com.threadly.notification.core.domain.mail.model.WelcomeModel;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
import com.threadly.notification.core.port.mail.out.SendMailPort;
import com.threadly.notification.core.service.utils.MailModelMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * SendMailService 테스트
 */
@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {

  @InjectMocks
  private SendMailService sendMailService;

  @Mock
  private SendMailPort sendMailPort;

  @Mock
  private MailModelMapper mailModelMapper;

  @Mock
  private TemplateEngine templateEngine;

  @Nested
  @DisplayName("가입 환영 메일 전송 테스트")
  class SendWelcomeMailTest {

    /*[Case #1] 가입 환영 메일이 정상적으로 전송되어야 한다*/
    @DisplayName("1. 가입 환영 메일이 정상적으로 전송되는지 검증")
    @Test
    void send_shouldDispatchWelcomeMail_whenTypeIsWelcome() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          MailType.WELCOME,
          "welcome@threadly.io",
          Map.of("userName", "홍길동", "loginUrl", "https://threadly.io/login")
      );
      when(mailModelMapper.toTypeModel(command))
          .thenReturn(new WelcomeModel("홍길동", "https://threadly.io/login"));
      when(templateEngine.process(eq("signup-complete-mail"), any(Context.class)))
          .thenReturn("rendered-welcome");

      //when
      sendMailService.send(command);

      //then
      verify(sendMailPort).sendMail(
          eq("welcome@threadly.io"),
          eq("[홍길동] 님 가입을 환엽합니다."),
          eq("rendered-welcome")
      );
    }
  }

  @Nested
  @DisplayName("이메일 인증 메일 전송 테스트")
  class SendVerificationMailTest {

    /*[Case #1] 인증 메일이 정상적으로 전송되어야 한다*/
    @DisplayName("1. 인증 메일이 정상적으로 전송되는지 검증")
    @Test
    void send_shouldDispatchVerificationMail_whenTypeIsVerification() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          MailType.VERIFICATION,
          "verify@threadly.io",
          Map.of("userName", "홍길동", "verificationUrl", "https://threadly.io/verify?code=1234")
      );
      when(mailModelMapper.toTypeModel(command))
          .thenReturn(new VerificationModel("홍길동", "https://threadly.io/verify?code=1234"));
      when(templateEngine.process(eq("verify-email-mail"), any(Context.class)))
          .thenReturn("rendered-verification");

      //when
      sendMailService.send(command);

      //then
      verify(sendMailPort).sendMail(
          eq("verify@threadly.io"),
          eq("[Threadly] 본인 인증을 위한 이메일입니다."),
          eq("rendered-verification")
      );
    }

    /*[Case #2] 전송 실패 시 EMAIL_SENDING_FAILED 예외가 발생해야 한다*/
    @DisplayName("2. 메일 전송 실패 시 EMAIL_SENDING_FAILED 예외가 발생하는지 검증")
    @Test
    void send_shouldThrowEmailVerificationException_whenMailSendingFails() throws Exception {
      //given
      SendMailCommand command = new SendMailCommand(
          MailType.VERIFICATION,
          "verify@threadly.io",
          Map.of("userName", "홍길동", "verificationUrl", "https://threadly.io/verify?code=1234")
      );
      when(mailModelMapper.toTypeModel(command))
          .thenReturn(new VerificationModel("홍길동", "https://threadly.io/verify?code=1234"));
      when(templateEngine.process(eq("verify-email-mail"), any(Context.class)))
          .thenReturn("rendered-verification");
      doThrow(new IllegalStateException("smtp down"))
          .when(sendMailPort)
          .sendMail(eq("verify@threadly.io"), eq("[Threadly] 본인 인증을 위한 이메일입니다."),
              eq("rendered-verification"));

      //when & then
      assertThatThrownBy(() -> sendMailService.send(command))
          .isInstanceOf(EmailVerificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.EMAIL_SENDING_FAILED);
    }
  }
}
