package com.example.sample.global.security.jwt;

import com.example.sample.global.error.CommonErrorCode;
import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        try {
            if (token != null) {
                // 1. 유효성 검증
                jwtTokenProvider.validateToken(token);

                // 2. 인증 객체 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            CommonErrorCode errorCode = SampleErrorCode.TOKEN_INVALID;
            if (e instanceof CommonException commonException) {
                errorCode = commonException.getErrorCode();
                log.warn("JWT Validation Warning: {}", errorCode.getMessage());
            } else {
                log.error("Unhandled JWT Exception", e);
            }
            request.setAttribute(JwtConstants.EXCEPTION_ATTRIBUTE, errorCode);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 헤더에서 토큰 추출
     * @param request
     * @return
     */
    private String resolveToken(HttpServletRequest request) {
        // 1. 헤더에서 꺼낼 때 상수 사용 (HttpHeaders.AUTHORIZATION)
        String bearerToken = request.getHeader(JwtConstants.ACCESS_TOKEN_HEADER);

        // 2. 접두사 체크할 때 상수 사용 ("Bearer ")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
        }

        return null;
    }
}
