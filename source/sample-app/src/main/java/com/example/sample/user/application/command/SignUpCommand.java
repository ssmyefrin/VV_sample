package com.example.sample.user.application.command;

import lombok.Builder;

/**
 * SignUpCommand
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Builder
public record SignUpCommand(
        String username,
        String password,
        String displayName,
        String email
) {}