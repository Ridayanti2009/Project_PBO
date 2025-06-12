// src/main/java/com/example/demo/service/UserService.java
package com.example.demo.service;

import java.util.Optional;

import com.example.demo.dto.UpdateProfileRequest; // <-- IMPORT BARU
import com.example.demo.model.User;

public interface UserService {
    // Method yang sudah ada
    User findByUsernameAndPassword(String username, String password);
    // User findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);

    // 👇 METHOD BARU YANG KITA TAMBAHKAN
    Optional<User> findUserById(Long id);
    void updateUserProfile(Long id, UpdateProfileRequest request);

    long countTotalUsers();
}