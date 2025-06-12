// src/main/java/com/example/demo/service/UserServiceImpl.java
package com.example.demo.service;

import com.example.demo.dto.UpdateProfileRequest; // <-- IMPORT BARU
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional; // <-- IMPORT BARU

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Method yang sudah ada
    @Override
    public User findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 👇 IMPLEMENTASI METHOD BARU
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public void updateUserProfile(Long id, UpdateProfileRequest request) {
        // Cari user berdasarkan ID
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update data dari DTO
            user.setNama(request.getNama());
            user.setEmail(request.getEmail());
            user.setNomorTelepon(request.getNomorTelepon());
            user.setAlamat(request.getAlamat());
            user.setTanggalLahir(request.getTanggalLahir());

            // Simpan perubahan ke database
            userRepository.save(user);
        } else {
            // Bisa tambahkan error handling jika user tidak ditemukan
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @Override
    public long countTotalUsers() {
        return userRepository.count(); // Menghitung semua user
    }
}