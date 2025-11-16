package com.threadly.notification.adapter.smtp.client;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SandBoxMailSmtpAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
class SandBoxMailSmtpAdapterTest {

  @InjectMocks
  private SandBoxMailSmtpAdapter sandBoxMailSmtpAdapter;

  @Mock
  private MailClient mailClient;

  /*[Case #1] 실제 수신자와 무관하게 고정된 수신자로 메일이 전송되어야 한다*/
  @DisplayName("1. 고정된 수신자로 메일 전송이 위임되는지 검증")
  @Test
  void sendMail_shouldDelegateToMailClientWithFixedRecipient() throws Exception {
    //when
    sandBoxMailSmtpAdapter.sendMail("user@threadly.io", "subject", "body");

    //then
    verify(mailClient).sendMail("rlarbqor00@naver.com", "rlarbqor00@naver.com", "subject", "body");
  }

  /*[Case #2] 다른 수신자를 전달해도 고정된 수신자로 메일이 전송되어야 한다*/
  @DisplayName("2. 다른 수신자를 전달해도 고정된 수신자로 메일이 전송되는지 검증")
  @Test
  void sendMail_shouldAlwaysUseFixedRecipient() throws Exception {
    //when
    sandBoxMailSmtpAdapter.sendMail("another@example.com", "test subject", "test body");

    //then
    verify(mailClient).sendMail("rlarbqor00@naver.com", "rlarbqor00@naver.com", "test subject", "test body");
  }
}
