package com.example.sample.user.infrastructure;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.user.domain.User;
import com.example.sample.user.domain.UserStore;
import com.example.sample.user.infrastructure.entity.UserEntity;
import com.example.sample.user.infrastructure.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStoreImpl implements UserStore {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    private static final String UK_USERS_USERNAME = "UK_USERS_USERNAME";
    private static final String UK_USERS_EMAIL = "UK_USERS_EMAIL";

    /**
     * 신규등록
     * @param user
     */
    @Override
    public void create(User user) {
        try {
            UserEntity entity = userEntityMapper.toEntity(user);
            userJpaRepository.save(entity);
            userJpaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            if (isConstraintViolation(e, UK_USERS_USERNAME)) {
                throw new CommonException(SampleErrorCode.DUPLICATE_USERNAME);
            }
            if (isConstraintViolation(e, UK_USERS_EMAIL)) {
                throw new CommonException(SampleErrorCode.DUPLICATE_USEREMAIL);
            }
            throw e;
        }
    }

    /**
     * 수정
     * @param user
     */
    @Override
    public void update(User user) {
        if (user.getId() == null || !userJpaRepository.existsById(user.getId())) {
            throw new CommonException(SampleErrorCode.USER_NOT_FOUND);
        }
        UserEntity entity = userEntityMapper.toEntity(user);
        userJpaRepository.save(entity);

    }

    /**
     * 삭제로 상태변경
     * @param user
     */
    @Override
    public void softDelete(User user) {
        UserEntity entity = userEntityMapper.toEntity(user);
        userJpaRepository.save(entity);
    }

    /**
     * 제약 조건 헬퍼
     * @param e
     * @param constraintName
     * @return
     */
    private boolean isConstraintViolation(DataIntegrityViolationException e, String constraintName) {
        Throwable cause = e.getMostSpecificCause();
        if (cause == null || cause.getMessage() == null) {
            return false;
        }
        return cause.getMessage().contains(constraintName);
    }

}