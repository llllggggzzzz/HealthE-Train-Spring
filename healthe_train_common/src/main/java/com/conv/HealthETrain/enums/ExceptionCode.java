package com.conv.HealthETrain.enums;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ExceptionCode {
    BAD_REQUEST(400, "错误的请求"),
    IMAGE_UPLOAD_ERROR(402, "图床配置异常"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "未找到"),
    JELLYFIN_INIT_ERROR(405, "JELLYFIN初始化异常"),
    FILE_PATH_ERROR(406, "路径异常");



    @JsonValue
    private final Integer code;
    private final String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
