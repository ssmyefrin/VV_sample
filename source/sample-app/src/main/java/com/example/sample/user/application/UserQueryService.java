package com.example.sample.user.application;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.user.application.dto.UserInfoResult;
import com.example.sample.user.domain.User;
import com.example.sample.user.domain.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserQueryService
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserReader userReader;

    public UserInfoResult getUserInfo(String username) {
        User user = userReader.getByUsername(username);
        return UserInfoResult.from(user);
    }
}
