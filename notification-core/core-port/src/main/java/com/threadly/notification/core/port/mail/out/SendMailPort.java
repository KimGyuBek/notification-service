package com.threadly.notification.core.port.mail.out;

/**
 * 메일 전송 port
 */
public interface SendMailPort {

  /**
   * 메일 전송
   *
   * @param subject
   * @param context
   */
  void sendMail(String to, String subject, String context);
}
