package com.threadly.notification.adapter.smtp.client;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
import com.threadly.notification.core.port.mail.out.SendMailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailClient implements SendMailPort {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  private final static String FROM = "rlarbqor00@naver.com";

  @Value("${properties.email.verification-url}")
  private String emailVerificationUrl;

  @Override
  public void sendVerificationMail(String to, String code) {

    String verifyUrl = emailVerificationUrl + "/api/auth/verify-email?code=" + code;

    Map<String, Object> values = new HashMap<>();
    values.put("verifyUrl", verifyUrl);

    String subject = "[Threadly] 본인 인증을 위한 메일입니다.";
    String context = getContext(values, "verify-email-mail");

    try {
      sendMail(subject, context);

      log.info("인증 메일 전송 완료");
      log.debug("verifyUrl : " + verifyUrl);

    } catch (Exception e) {
      throw new EmailVerificationException(ErrorCode.EMAIL_SENDING_FAILED);
    }

  }

  @Override
  public void sendWelcomeMail(String to, String userName) {
    try {
      String subject = "[" + userName + "] 님 가입을 진심으로 환영합니다.";
      String context = getContext(null, "signup-complete-mail");

      sendMail(subject, context);

      log.info("인증 확인 메일 전송 완료");
    } catch (Exception e) {
      throw new EmailVerificationException(ErrorCode.EMAIL_SENDING_FAILED);
    }

  }

  /**
   * mail 전송
   *
   * @param subject
   * @param context
   * @throws MessagingException
   */
  private void sendMail(String subject, String context)
      throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    helper.setFrom(FROM);
    helper.setTo(FROM);
    helper.setSubject(subject);
    helper.setText(context, true);

    mailSender.send(mimeMessage);
  }


  private String getContext(Map<String, Object> values, String template) {
    Context context = new Context();
    context.setVariables(values);

    return templateEngine.process(template, context);
  }

}
