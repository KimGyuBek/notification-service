package com.threadly.notification.adapter.smtp.client;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
import com.threadly.notification.core.port.mail.out.SendMailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailClient implements SendMailPort {

  private final JavaMailSender mailSender;

  private final static String FROM = "rlarbqor00@naver.com";

  /**
   * 메일 전송
   *
   * @param to
   * @param subject
   * @param context
   * @throws MessagingException
   */
  @Override
  public void sendMail(String to, String subject, String context) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      helper.setFrom(FROM);
      /*TODO to 수정*/
      helper.setTo(FROM);
      helper.setSubject(subject);
      helper.setText(context, true);

      mailSender.send(mimeMessage);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new EmailVerificationException(ErrorCode.EMAIL_SENDING_FAILED);
    }
  }
}
