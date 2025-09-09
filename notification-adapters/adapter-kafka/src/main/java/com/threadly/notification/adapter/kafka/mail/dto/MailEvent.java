package com.threadly.notification.adapter.kafka.mail.dto;


import com.threadly.notification.core.domain.mail.MailType;
import com.threadly.notification.core.port.mail.in.dto.SendMailCommand;
import java.util.Map;

/**
 * Kafka Mail event 객체
 */
public record MailEvent(
    String eventId,
    MailType mailType,
    String to,
    Map<String, Object> model
) {

  /**
   * event -> command
   *
   * @return
   */
  public SendMailCommand toCommand() {
    return new SendMailCommand(
        this.mailType,
        this.to,
        this.model
    );
  }

}
