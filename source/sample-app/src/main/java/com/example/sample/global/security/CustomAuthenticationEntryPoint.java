package com.example.sample.global.security;

import com.example.sample.global.common.ApiResult;
import com.example.sample.global.error.CommonErrorCode;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.global.security.jwt.JwtConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // 1. JwtAuthenticationFilter에서 넣어둔 에러 코드 꺼내기
        Object exception = request.getAttribute(JwtConstants.EXCEPTION_ATTRIBUTE);
        CommonErrorCode errorCode;

        if (exception instanceof CommonErrorCode) {
            // 필터에서 우리가 의도적으로 잡은 에러 (만료, 위조 등)
            errorCode = (CommonErrorCode) exception;
        } else {
            // 그 외에 시큐리티 체인 타다가 알 수 없는 이유로 막힌 경우 (토큰 없음, URL 잘못됨 등)
            // 보통 토큰 없이 접근하면 여기가 걸림 -> 4002 유효하지 않은 토큰 처리
            errorCode = SampleErrorCode.TOKEN_INVALID;
        }

        // 2. 로그 남기기 (디버깅용, WARN 정도가 적당)
        log.warn("Authentication Failed: URL: {}, ErrorCode: {}, Message: {}",
                request.getRequestURI(), errorCode.getCode(), errorCode.getMessage());

        // 3. JSON 응답 전송
        sendErrorResponse(response, errorCode);
    }

    private void sendErrorResponse(HttpServletResponse response, CommonErrorCode errorCode) throws IOException {

        // 헤더 설정
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value()); // 401

        // 바디 설정 (ApiResult 양식 그대로!)
        ApiResult<Void> body = ApiResult.nok(errorCode);

        // JSON 변환 및 쓰기
        String jsonResult = objectMapper.writeValueAsString(body);
        response.getWriter().write(jsonResult);
    }
}