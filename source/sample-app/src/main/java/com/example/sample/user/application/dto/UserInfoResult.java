package com.example.sample.user.application.dto;

import com.example.sample.user.domain.User;
import com.example.sample.user.domain.UserRole;

/**
 * UserInfoResult
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
public record UserInfoResult(
        String username,
        String displayName,
        String email,
        UserRole role
) {
    public static UserInfoResult from(User user) {
        return new UserInfoResult(
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole()
        );
    }
}