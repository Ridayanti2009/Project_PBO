package com.example.demo.service;

import com.example.demo.model.User;

public interface UserService {
    User findByUsernameAndPassword(String username, String password); 

    User save(User user);
    boolean existsByUsername(String username);
}

