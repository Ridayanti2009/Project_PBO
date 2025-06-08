// src/main/java/com/example/demo/service/UserService.java
package com.example.demo.service;

import com.example.demo.dto.UpdateProfileRequest; // <-- IMPORT BARU
import com.example.demo.model.User;
import java.util.Optional; // <-- IMPORT BARU

public interface UserService {
    // Method yang sudah ada
    User findByUsernameAndPassword(String username, String password);
    User save(User user);
    boolean existsByUsername(String username);

    // 👇 METHOD BARU YANG KITA TAMBAHKAN
    Optional<User> findUserById(Long id);
    void updateUserProfile(Long id, UpdateProfileRequest request);
}