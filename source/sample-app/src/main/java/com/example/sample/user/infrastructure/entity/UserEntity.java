package com.example.sample.user.infrastructure.entity;

import com.example.sample.global.common.domain.BaseEntity;
import com.example.sample.user.domain.UserRole;
import com.example.sample.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_USERS_USERNAME", columnNames = "USERNAME"),
                @UniqueConstraint(name = "UK_USERS_EMAIL", columnNames = "EMAIL")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    private LocalDateTime lastLoginAt;

}