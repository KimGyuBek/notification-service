package com.threadly.notification.adapter.websocket.notification.dto;

public record ResyncRequest(
    InboundMessageType type,
    String afterId,
    int limit
) implements WsInboundMessage {

}
