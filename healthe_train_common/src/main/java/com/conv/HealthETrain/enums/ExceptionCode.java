package com.conv.HealthETrain.enums;
import lombok.Getter;

@Getter
public enum ExceptionCode {
    IMAGE_UPLOAD_ERROR(101, "图床配置异常"),
    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未授权");


    private final Integer code;
    private final String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
