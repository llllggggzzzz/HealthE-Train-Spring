package com.conv.HealthETrain.exception;

import com.conv.HealthETrain.enums.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private ExceptionCode errorCode;

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, ExceptionCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


}
