package com.example.sample.global.security.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Token
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Builder
public record Token(
        String grantType,
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn
) {
}
