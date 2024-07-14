package com.conv.HealthETrain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType {
    BROADCAST_MSG(1,"群发"),
    PRIVATE_MSG(2, "私发"),
    NOTE_SHARE(3, "笔记分享"),
    ACK_NOTE(4, "确认消息");

    @JsonValue
    private final Integer code;
    private final String message;

    MessageType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
