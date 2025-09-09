package com.threadly.notification.core.port.mail.in.dto;

import com.threadly.notification.core.domain.mail.MailType;
import java.util.Map;

/**
 * 메일 전송 command
 */
public record SendMailCommand(
    MailType mailType,
    String to,
    Map<String, Object> model
) {

}
