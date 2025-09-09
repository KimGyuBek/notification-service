package com.threadly.notification.core.port.mail.in;

import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;

/**
 * 메일  이벤트 저장 처리 usecase
 */
public interface SendMailUseCase {

  void send(SendMailCommand command);
}
