package com.example.sample.user.infrastructure;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.user.domain.User;
import com.example.sample.user.domain.UserReader;
import com.example.sample.user.infrastructure.mapper.UserEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReaderImpl implements UserReader {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public User getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new CommonException(SampleErrorCode.USER_NOT_FOUND));
    }

    @Override
    public boolean isDuplicateUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean isDuplicateEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userEntityMapper::toDomain);
    }

}