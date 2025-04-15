package com.uams.service;

import com.uams.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User saveUser(User user);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}