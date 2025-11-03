package com.threadly.notification.core.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.notification.core.domain.mail.model.MailModel;
import com.threadly.notification.core.domain.mail.model.VerificationModel;
import com.threadly.notification.core.domain.mail.model.WelcomeModel;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mail model 매퍼
 */
@Component
@RequiredArgsConstructor
public class MailModelMapper {

  private final ObjectMapper objectMapper;

  /**
   * 주어진 type에 해당하는 model로 변경
   *
   * @param command
   * @return
   */
  public <T extends MailModel> T toTypeModel(SendMailCommand command) {
    Class<? extends MailModel> clazz = switch (command.mailType()) {
      case WELCOME -> WelcomeModel.class;
      case VERIFICATION -> VerificationModel.class;
    };

    return (T) objectMapper.convertValue(command.model(), clazz);
  }

}
