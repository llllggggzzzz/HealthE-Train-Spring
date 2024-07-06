package com.conv.HealthETrain.enums;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
public enum ResponseCode {
    SUCCEED(200, "成功"),
    CREATED(201, "已创建"),
    ACCEPTED(202, "已接受"),
    NO_CONTENT(204, "无内容"),
    MOVED_PERMANENTLY(301, "永久重定向"),
    FOUND(302, "临时重定向"),
    NOT_MODIFIED(304, "未修改"),
    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    CONFLICT(409, "冲突"),
    GONE(410, "资源已不存在"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    UNPROCESSABLE_ENTITY(422, "无法处理的实体"),
    TOO_MANY_REQUESTS(429, "请求过多"),
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    NOT_IMPLEMENTED(501, "未实现"),
    BAD_GATEWAY(502, "错误的网关"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时");


    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
