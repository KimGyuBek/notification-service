package com.threadly.notification.adapter.smtp.client;

import com.threadly.notification.core.port.mail.out.SendMailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSmtpAdapter implements SendMailPort {

  private final MailClient mailClient;

  @Override
  public void sendMail(String to, String subject, String context) {
    mailClient.sendMail(to, subject, context);
  }
}
