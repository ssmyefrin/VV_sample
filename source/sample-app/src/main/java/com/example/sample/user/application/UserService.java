package com.example.sample.user.application;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.global.security.dto.Token;
import com.example.sample.global.security.jwt.JwtTokenProvider;
import com.example.sample.user.application.command.ChangePasswordCommand;
import com.example.sample.user.application.command.LoginCommand;
import com.example.sample.user.application.command.SignUpCommand;
import com.example.sample.user.application.command.UpdateUserCommand;
import com.example.sample.user.application.dto.LoginResult;
import com.example.sample.user.domain.User;
import com.example.sample.user.domain.UserReader;
import com.example.sample.user.domain.UserStore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;
    private final UserStore userStore;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * @param command
     */
    @Transactional
    public void signUp(SignUpCommand command) {
        // 중복체크
        validateDuplicate(command.username(), command.email());
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(command.password());
        // 저장
        User user = User.create(
                command.username(),
                encodedPassword,
                command.displayName(),
                command.email());
        userStore.create(user);
    }

    /**
     * 로그인
     * @param command
     * @return
     */
    @Transactional
    public LoginResult login(LoginCommand command) {
        // 유저 상태 체크
        User user = userReader.getByUsername(command.username());
        if (!user.isActive()) throw new CommonException(SampleErrorCode.USER_NOT_ACTIVE);
        // 비밀번호 체크
        user.validatePassword(command.password(), passwordEncoder);
        // 접속시간 갱신
        User loggedInUser = user.loginSuccess();
        userStore.update(loggedInUser);
        // 토큰생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loggedInUser.getUsername(), "", List.of(new SimpleGrantedAuthority("ROLE_" + loggedInUser.getRole())));
        Token token = jwtTokenProvider.generateToken(authentication);
        return LoginResult.from(loggedInUser, token);
    }

    /**
     * 회원정보 수정
     * @param command
     */
    @Transactional
    public void updateInfo(UpdateUserCommand command) {
        // 회원조회
        User user = userReader.getByUsername(command.username());
        // 수정된 정보 저장
        User updatedUser = user.updateInfo(command.displayName(), command.email());
        userStore.update(updatedUser);
    }

    /**
     * 회원탈퇴(soft delete)
     * @param username
     */
    @Transactional
    public void withdraw(String username) {
        // 회원조회 및 탈퇴체크
        User user = userReader.getByUsername(username);
        User withdrawnUser = user.withdraw();
        // 탈퇴
        userStore.softDelete(withdrawnUser);
    }

    /**
     * 비밀번호 변경
     * @param command
     */
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        // 회원조회
        User user = userReader.getByUsername(command.username());
        // 현재 비번 검증
        user.validatePassword(command.oldPassword(), passwordEncoder);
        // 비번 변경
        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        User updatedUser = user.changePassword(encodedNewPassword);
        // 저장
        userStore.update(updatedUser);
    }

    /**
     * 중복체크
     * @param username
     * @param email
     */
    private void validateDuplicate(String username, String email) {
        if (userReader.isDuplicateUsername(username)) throw new CommonException(SampleErrorCode.DUPLICATE_USERNAME);
        if (userReader.isDuplicateEmail(email)) throw new CommonException(SampleErrorCode.DUPLICATE_USEREMAIL);
    }

}