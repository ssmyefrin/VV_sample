package com.example.sample.user.application.dto;

import com.example.sample.global.security.dto.Token;
import com.example.sample.user.domain.User;

/**
 * LoginResult
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
public record LoginResult(
                String username,
                String displayName,
                String email,
                Token token) {
        public static LoginResult from(User user, Token token) {
                return new LoginResult(
                                user.getUsername(),
                                user.getDisplayName(),
                                user.getEmail(),
                                token);
        }
}