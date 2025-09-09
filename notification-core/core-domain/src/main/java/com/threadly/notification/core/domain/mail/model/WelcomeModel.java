package com.threadly.notification.core.domain.mail.model;

/**
 * 환영 메일 model
 * @param userName
 * @param loginUrl
 */
public record WelcomeModel(
    String userName,
    String loginUrl
) implements MailModel {

  @Override
  public String userName() {
    return userName;
  }
}
