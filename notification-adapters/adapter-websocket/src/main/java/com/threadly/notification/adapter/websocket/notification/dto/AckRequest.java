package com.threadly.notification.adapter.websocket.notification.dto;

/**
 * ACK dto
 */
public record AckRequest(
    InboundMessageType type,
    String eventId

) implements WsInboundMessage {

}
