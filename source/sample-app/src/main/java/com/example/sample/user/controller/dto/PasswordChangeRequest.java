package com.example.sample.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * PasswordChangeRequest
 *
 * @author : hhh
 * @version 1.0
 * @date : 2/5/26
 */
@Schema(description = "비밀번호 변경 요청")
public record PasswordChangeRequest(
        @NotBlank @Schema(description = "현재 비밀번호") String oldPassword,
        @NotBlank @Schema(description = "새 비밀번호") String newPassword
) {}