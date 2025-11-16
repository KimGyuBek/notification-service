package com.threadly.notification.adapter.smtp.client;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
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
public class MailClient {

  private final JavaMailSender mailSender;

  /**
   * 메일 전송
   *
   * @param to
   * @param subject
   * @param context
   * @throws MessagingException
   */
  public void sendMail(String from, String to, String subject, String context) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(context, true);

      mailSender.send(mimeMessage);

      log.debug("mail 전송, to: {}", to);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new EmailVerificationException(ErrorCode.EMAIL_SENDING_FAILED);
    }
  }
}
