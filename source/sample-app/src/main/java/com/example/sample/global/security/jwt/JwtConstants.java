package com.example.sample.global.security.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

/**
 * JwtConstants
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtConstants {
    public static final String GRANT_TYPE = "Bearer";
    public static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String TOKEN_PREFIX = "Bearer "; // 뒤에 공백 포함
    public static final String AUTHORITIES_KEY = "auth"; // 권한 정보 키 (짧게!)
    public static final String EXCEPTION_ATTRIBUTE = "exception";
}
