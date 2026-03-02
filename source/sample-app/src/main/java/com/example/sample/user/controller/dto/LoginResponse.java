package com.example.sample.user.controller.dto;

import com.example.sample.global.security.dto.Token;
import com.example.sample.user.application.dto.LoginResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "사용자 아이디", example = "abc1234")
        String username,
        @Schema(description = "사용자 이름", example = "홍길동")
        String displayName,

        @Schema(description = "이메일", example = "dev@gmail.com")
        String email,

        @Schema(description = "JWT 토큰")
        Token token

) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.username(),
                result.displayName(),
                result.email(),
                result.token()
        );
    }

}