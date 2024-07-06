package com.conv.HealthETrain.enums;
import lombok.Getter;

@Getter
public enum ExceptionCode {
    IMAGE_TOKEN_EMPTY(101, "图床token解析失败"),
    UNAUTHORIZED(401, "未授权");

    private final Integer code;
    private final String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
