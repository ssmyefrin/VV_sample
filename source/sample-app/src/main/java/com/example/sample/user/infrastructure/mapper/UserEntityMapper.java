package com.example.sample.user.infrastructure.mapper;

import com.example.sample.global.common.mapper.EntityMapper;
import com.example.sample.user.domain.User;
import com.example.sample.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UserEntityMapper - Domain ↔ Entity 변환
 * 
 * @author : hhh
 * @version 1.0
 * @since 2026-02-04
 */
@Component
public class UserEntityMapper implements EntityMapper<User, UserEntity> {

    @Override
    public UserEntity toEntity(User domain) {
        if (domain == null)
            return null;

        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .displayName(domain.getDisplayName())
                .email(domain.getEmail())
                .role(domain.getRole())
                .status(domain.getStatus())
                .lastLoginAt(domain.getLastLoginAt())
                .build();

    }

    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null)
            return null;
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .displayName(entity.getDisplayName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .status(entity.getStatus())
                .lastLoginAt(entity.getLastLoginAt())
                .build();

    }

    @Override
    public List<User> toDomainList(List<UserEntity> entityList) {
        return entityList == null ? List.of() : entityList.stream().map(this::toDomain).toList();
    }

    @Override
    public List<UserEntity> toEntityList(List<User> domainList) {
        return domainList == null ? List.of() : domainList.stream().map(this::toEntity).toList();
    }
}