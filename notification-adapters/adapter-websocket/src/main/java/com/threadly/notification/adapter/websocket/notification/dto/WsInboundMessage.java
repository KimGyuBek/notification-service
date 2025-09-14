package com.threadly.notification.adapter.websocket.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * InboundMessage 인터페이스
 */
@JsonTypeInfo(
    use = Id.NAME,
    include = As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AckRequest.class, name = "ACK"),
    @JsonSubTypes.Type(value = ResyncRequest.class, name = "RESYNC")
})
public sealed interface WsInboundMessage
    permits AckRequest, ResyncRequest {

  InboundMessageType type();

  enum InboundMessageType {
    ACK, RESYNC
  }

}
