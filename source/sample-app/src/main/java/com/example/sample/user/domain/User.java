package com.example.sample.user.domain;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder(toBuilder = true)
public class User {
    private final Long id;
    private final String username;
    private final String password;
    private final String displayName;
    private final String email;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime lastLoginAt;
    
    /**
     * 사용자 생성
     * @param username
     * @param password
     * @param displayName
     * @param email
     * @return
     */
    public static User create(String username, String password, String displayName, String email) {
        return User.builder()
                .username(username)
                .password(password)
                .displayName(displayName)
                .email(email)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /**
     * 비밀번호 검증
     * @param rawPassword
     * @param encoder
     */
    public void validatePassword(String rawPassword, PasswordEncoder encoder) {
        if (!encoder.matches(rawPassword, password)) {
            throw new CommonException(SampleErrorCode.LOGIN_FAILED);
        }
    }

    /**
     * 사용자 활성 상태 여부
     * @return
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * 사용자 정보 업데이트
     * @param changedDisplayName
     * @param changedEmail
     * @return
     */
    public User updateInfo(String changedDisplayName, String changedEmail) {
        return this.toBuilder()
                .displayName(changedDisplayName != null ? changedDisplayName : this.displayName)
                .email(changedEmail != null ? changedEmail : this.email)
                .build();
    }

    /**
     * 비밀번호 변경
     * @param newEncodedPassword
     * @return
     */
    public User changePassword(String newEncodedPassword) {
        return this.toBuilder()
                .password(newEncodedPassword)
                .build();
    }

    /**
     * 로그인 성공
     * @return
     */
    public User loginSuccess() {
        return this.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    /**
     * 회원 탈퇴
     * @return
     */
    public User withdraw() {
        // 이미 탈퇴한 경우
        if (this.status == UserStatus.DELETED)
            throw new CommonException(SampleErrorCode.ALREADY_WITHDRAWN);
        return this.toBuilder()
                .status(UserStatus.DELETED)
                .build();
    }


}