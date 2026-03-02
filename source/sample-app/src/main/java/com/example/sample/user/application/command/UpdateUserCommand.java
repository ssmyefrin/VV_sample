package com.example.sample.user.application.command;

import lombok.Builder;

/**
 * UpdateUserCommand
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Builder
public record UpdateUserCommand(
        String username,
        String displayName,
        String email
) {}