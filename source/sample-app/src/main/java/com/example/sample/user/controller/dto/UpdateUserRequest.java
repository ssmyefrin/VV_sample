package com.example.sample.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 
 * UpdateUserRequest
 *
 * @author : hhh
 * @version 1.0
 *  @since 2026-02-03
 */
@Builder
@Schema(description = "회원 정보 수정 요청")
public record UpdateUserRequest(
        @Schema(description = "변경할 이름", example = "개굴개굴") @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.") String displayName,
        @Schema(description = "변경할 이메일", example = "frog@example.com") @Email(message = "이메일 형식이 올바르지 않습니다.") String email){
}