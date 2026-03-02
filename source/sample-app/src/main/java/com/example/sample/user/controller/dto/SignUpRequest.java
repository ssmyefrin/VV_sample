package com.example.sample.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.sample.user.application.command.SignUpCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @NotBlank @Schema(description = "사용자 아이디", example = "abc1234") String username,

        @NotBlank @Schema(description = "비밀번호") String password,

        @NotBlank @Schema(description = "사용자 이름", example = "홍길동") String displayName,

        @Email @Schema(description = "이메일", example = "dev@gmail.com") String email) {

}