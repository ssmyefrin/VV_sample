package com.example.sample.user.domain;

import java.util.Optional;

public interface UserReader {
    Optional<User> findByUsername(String username);
    User getByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean isDuplicateUsername(String username);
    boolean isDuplicateEmail(String email);
}