package com.example.sample.user.controller.dto;

import com.example.sample.user.application.dto.UserInfoResult;
import com.example.sample.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoResponse(
                @Schema(description = "로그인 아이디", example = "abc1234") String username,

                @Schema(description = "사용자 이름", example = "홍길동") String displayName,

                @Schema(description = "이메일", example = "dev@gmail.com") String email,

                @Schema(description = "권한", example = "USER") UserRole role) {
        public static UserInfoResponse from(UserInfoResult result) {
                return new UserInfoResponse(
                                result.username(),
                                result.displayName(),
                                result.email(),
                                result.role());
        }
}