package com.example.sample.user.application.command;

/**
 * ChangePasswordCommand
 *
 * @author : hhh
 * @version 1.0
 * @date : 2/5/26
 */
public record ChangePasswordCommand(
        String username,
        String oldPassword,
        String newPassword
) {
}
