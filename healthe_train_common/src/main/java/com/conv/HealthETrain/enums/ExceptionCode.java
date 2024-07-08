package com.conv.HealthETrain.enums;
import lombok.Getter;

@Getter
public enum ExceptionCode {
    BAD_REQUEST(400, "错误的请求"),
    IMAGE_UPLOAD_ERROR(402, "图床配置异常"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "未找到");



    private final Integer code;
    private final String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
