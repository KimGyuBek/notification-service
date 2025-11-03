package com.threadly.notification.adapter.smtp.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * MailClient 테스트
 */
@ExtendWith(MockitoExtension.class)
class MailClientTest {

  @InjectMocks
  private MailClient mailClient;

  @Mock
  private JavaMailSender javaMailSender;

  private MimeMessage mimeMessage() {
    return new MimeMessage((Session) null);
  }

  @Nested
  @DisplayName("sendMail 테스트")
  class SendMailTest {

    /*[Case #1] 메일 전송이 성공적으로 수행되어야 한다*/
    @DisplayName("1. 메일 전송이 성공적으로 수행되는지 검증")
    @Test
    void sendMail_shouldInvokeJavaMailSender() throws Exception {
      //given
      MimeMessage mimeMessage = mimeMessage();
      when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

      //when
      mailClient.sendMail("user@threadly.io", "subject", "<p>body</p>");

      //then
      verify(javaMailSender).send(mimeMessage);
    }

    /*[Case #2] 메일 전송 중 예외가 발생하면 EMAIL_SENDING_FAILED 예외가 발생해야 한다*/
    @DisplayName("2. 메일 전송 중 예외 발생 시 EMAIL_SENDING_FAILED 예외가 발생하는지 검증")
    @Test
    void sendMail_shouldThrowEmailVerificationException_whenSendFails() throws Exception {
      //given
      MimeMessage mimeMessage = mimeMessage();
      when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
      doThrow(new IllegalStateException("smtp error")).when(javaMailSender).send(mimeMessage);

      //when & then
      assertThatThrownBy(() -> mailClient.sendMail("user@threadly.io", "subject", "body"))
          .isInstanceOf(EmailVerificationException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.EMAIL_SENDING_FAILED);
    }
  }
}
