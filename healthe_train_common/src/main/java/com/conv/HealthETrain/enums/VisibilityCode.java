package com.conv.HealthETrain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VisibilityCode {
    V_PRIVATE(0,"私有"),
    V_PARTPUBLIC(1,"局部公开"),
    V_FULLPUBLIC(2,"完全公开");


    @JsonValue
    private final Integer code;
    private final String message;

    VisibilityCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}