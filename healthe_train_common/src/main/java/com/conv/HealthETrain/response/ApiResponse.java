package com.conv.HealthETrain.response;

import com.conv.HealthETrain.enums.ResponseCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private ResponseCode code;
    private String message;
    private T data;

    // 成功的响应（带数据）
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCEED, ResponseCode.SUCCEED.getMessage(), data);
    }

    // 成功的响应（无数据）
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.SUCCEED, ResponseCode.SUCCEED.getMessage(), null);
    }

    // 成功的响应（使用自定义状态吗）
    public static <T> ApiResponse<T> success(ResponseCode code) {
        return new ApiResponse<>(code, code.getMessage(), null);
    }

    // 成功的响应（使用自定义状态码以及自定义消息）
    public static <T> ApiResponse<T> success(ResponseCode code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // 成功的响应（无数据）
    public static <T> ApiResponse<T> success(ResponseCode code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    // 失败的响应（带错误码和自定义消息）
    public static <T> ApiResponse<T> error(ResponseCode errorCode, String message) {
        return new ApiResponse<>(errorCode, message, null);
    }

    // 失败的响应（带错误码，使用默认消息）
    public static <T> ApiResponse<T> error(ResponseCode errorCode) {
        return new ApiResponse<>(errorCode, errorCode.getMessage(), null);
    }

    // 失败的响应（带错误码，自定义消息以及自定义数据）
    public static <T> ApiResponse<T> error(ResponseCode errorCode, String message, T data) {
        return new ApiResponse<>(errorCode, message, data);
    }
}
