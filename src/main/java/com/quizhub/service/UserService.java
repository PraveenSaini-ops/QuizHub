package com.quizhub.service;

import com.quizhub.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(String name, String email, String rawPassword, com.quizhub.model.Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    User getCurrentUser();
}
