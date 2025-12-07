package com.threadly.notification.core.service.mail;

import com.threadly.notification.commons.exception.ErrorCode;
import com.threadly.notification.commons.exception.mail.EmailVerificationException;
import com.threadly.notification.core.domain.mail.model.VerificationModel;
import com.threadly.notification.core.domain.mail.model.WelcomeModel;
import com.threadly.notification.core.port.mail.in.SendMailUseCase;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
import com.threadly.notification.core.port.mail.out.SendMailPort;
import com.threadly.notification.core.service.utils.MailModelMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMailService implements SendMailUseCase {

  private final SendMailPort sendMailPort;
  private final MailModelMapper mailModelMapper;
  private final TemplateEngine templateEngine;

  @Override
  public void send(SendMailCommand command) {
    switch (command.mailType()) {
      case WELCOME -> {
        log.info("가입 환영 메일 전송: mailType={}, to={}", command.mailType(), command.to());
        sendWelcomeMail(command);
      }
      case VERIFICATION -> {
        log.info("인증 메일 전송: mailType={}, to={}", command.mailType(), command.to());
        sendVerificationMail(command);
      }
    }
  }

  /**
   * 가입 환영 메일 전송
   *
   * @param command
   */
  private void sendWelcomeMail(SendMailCommand command) {
    WelcomeModel model = mailModelMapper.toTypeModel(command);

    String subject = "[" + model.userName() + "] 님 가입을 환엽합니다.";
    String context = getContext(null, "signup-complete-mail");

    sendMail(command.to(), subject, context);
  }

  /**
   * 인증 메일 전송
   */
  private void sendVerificationMail(SendMailCommand command) {
    VerificationModel model = mailModelMapper.toTypeModel(command);

    Map<String, Object> values = new HashMap<>();
    values.put("verifyUrl", model.verificationUrl());

    String subject = "[Threadly] 본인 인증을 위한 이메일입니다.";
    String context = getContext(values, "verify-email-mail");

    sendMail(command.to(), subject, context);
  }

  /**
   * 메일 발송
   *
   * @param to
   * @param subject
   * @param context
   */
  private void sendMail(String to, String subject, String context) {
    try {
      sendMailPort.sendMail(to, subject, context);
    } catch (Exception e) {
      log.error("메일 전송 실패, error={}", e.getMessage());
      throw new EmailVerificationException(ErrorCode.EMAIL_SENDING_FAILED);
    }
  }

  /**
   * 주어진 map으로 context 생성
   *
   * @param values
   * @param template
   * @return
   */
  private String getContext(Map<String, Object> values, String template) {
    Context context = new Context();
    context.setVariables(values);

    return templateEngine.process(template, context);
  }
}
