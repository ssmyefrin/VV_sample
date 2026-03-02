package com.example.sample.user.controller.mapper;
import com.example.sample.user.application.command.ChangePasswordCommand;
import com.example.sample.user.application.command.LoginCommand;
import com.example.sample.user.application.command.SignUpCommand;
import com.example.sample.user.application.command.UpdateUserCommand;
import com.example.sample.user.controller.dto.*;
import org.springframework.stereotype.Component;

/**
 *
 * UserWebMapper
 *
 * @author : hhh
 * @version 1.0
 *  @since 2026-02-03
 */
@Component
public class UserWebMapper {
    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.username(), request.password());
    }

    public SignUpCommand toCommand(SignUpRequest request) {
        return new SignUpCommand(
                request.username(),
                request.password(),
                request.displayName(),
                request.email()
        );
    }

    public UpdateUserCommand toCommand(String username, UpdateUserRequest request) {
        return new UpdateUserCommand(
                username,
                request.displayName(),
                request.email()
        );
    }

    public ChangePasswordCommand toCommand(String username, PasswordChangeRequest request) {
        return new ChangePasswordCommand(
                username,
                request.oldPassword(),
                request.newPassword()
        );
    }
}