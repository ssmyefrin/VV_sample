package com.example.sample.global.common;

import com.example.sample.global.error.CommonErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * ApiResult
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final T data;
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> ok() {
        return ApiResult.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("success")
                .build();
    }

    public static <T> ApiResult<T> nok(CommonErrorCode errorCode) {
        return ApiResult.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static <T> ApiResult<T> nok(CommonErrorCode errorCode, String message) {
        return ApiResult.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(message)
                .build();
    }
}