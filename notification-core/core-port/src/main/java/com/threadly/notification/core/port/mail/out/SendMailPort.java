package com.threadly.notification.core.port.mail.out;

/**
 * 메일 전송 port
 */
public interface SendMailPort {

  void sendVerificationMail(String to, String code);

  void sendWelcomeMail(String to, String userName);

}
