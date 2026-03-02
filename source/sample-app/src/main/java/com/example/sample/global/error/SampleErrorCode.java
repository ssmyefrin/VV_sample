package com.example.sample.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 
 * - SampleErrorCode
 * -
 * - @author : hhh
 * - @version 1.0
 * - @date : 1/31/26
 */
@Getter
@AllArgsConstructor
public enum SampleErrorCode implements CommonErrorCode {

    /** JWT 인증 관련 에러 **/
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 4001, "만료된 토큰입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 4002, "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, 4003, "잘못된 토큰 형식입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, 4004, "지원하지 않는 토큰입니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, 4005, "토큰 서명이 유효하지 않습니다."),
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, 4006, "토큰이 비어있습니다."),
    TOKEN_NO_AUTHORITY(HttpStatus.UNAUTHORIZED, 4007, "토큰에 권한 정보가 없습니다."),

    /** 권한 관련 에러 **/
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 4008, "접근 권한이 없습니다."),

    /** 로그인 관련 에러 **/
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 5000, "아이디 또는 비밀번호가 일치하지 않습니다."),

    /** 사용자 관련 에러 **/
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 6000, "존재하지 않는 사용자입니다."),
    ALREADY_WITHDRAWN(HttpStatus.CONFLICT, 6001, "이미 탈퇴한 회원입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, 6002, "이미 존재하는 아이디입니다."),
    DUPLICATE_USEREMAIL(HttpStatus.CONFLICT, 6003, "이미 존재하는 Email입니다."),
    USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, 6004, "활성화된 사용자가 아닙니다."),

    /** 공지사항 관련 에러 **/
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, 6100, "존재하지 않는 공지사항입니다."),

    /** 공통 에러 **/
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 7000, "입력값 오류 입니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, 7001, "잘못된 JSON 형식입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 7002, "지원하지 않는 HTTP Method입니다."),

    /** 시스템 에러 **/
    COMMON_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "일시적인 시스템 오류입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}