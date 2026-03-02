package com.example.sample.global.error;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final CommonErrorCode errorCode;

    public CommonException(CommonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
