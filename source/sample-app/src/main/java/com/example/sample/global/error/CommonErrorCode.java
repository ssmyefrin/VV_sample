package com.example.sample.global.error;

import org.springframework.http.HttpStatus;

/**
 * CommonErrorCode
 *
 * @author : HHH
 * @version 1.0
 * @date : 2026-01-31
 */
public interface CommonErrorCode {
    HttpStatus getHttpStatus(); // HTTP 응답용
    int getCode();              // 내부 관리용 (9999 등)
    String getMessage();
}
