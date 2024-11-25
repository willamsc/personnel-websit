package com.willamsc.personalwebsite.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author william
 * @since 2024-11-25
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final String message;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
