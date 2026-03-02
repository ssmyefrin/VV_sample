package com.example.sample.user.domain;

public interface UserStore {
    void create(User user);
    void update(User user);
    void softDelete(User user);
}