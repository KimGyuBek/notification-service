package com.threadly.notification.adapter.smtp.client;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * MailSmtpAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class MailSmtpAdapterTest {

  @InjectMocks
  private MailSmtpAdapter mailSmtpAdapter;

  @Mock
  private MailClient mailClient;

  /*[Case #1] MailClient 에게 위임되어야 한다*/
  @DisplayName("1. MailClient 에게 메일 전송이 위임되는지 검증")
  @Test
  void sendMail_shouldDelegateToMailClient() throws Exception {
    //when
    mailSmtpAdapter.sendMail("user@threadly.io", "subject", "body");

    //then
    verify(mailClient).sendMail("threadly@naver.com", "user@threadly.io", "subject", "body");
  }
}
