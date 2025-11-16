package com.threadly.notification.adapter.smtp.client;

import com.threadly.notification.core.port.mail.out.SendMailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Dev, Test 전용 수신자 고정
 */
@Profile({"test", "dev"})
@Component
@RequiredArgsConstructor
public class SandBoxMailSmtpAdapter implements SendMailPort {

  private final MailClient mailClient;

  private static String FROM = "rlarbqor00@naver.com";
  private static String TO = "rlarbqor00@naver.com";

  @Override
  public void sendMail(String to, String subject, String context) {
    mailClient.sendMail(FROM, TO, subject, context);

  }
}
