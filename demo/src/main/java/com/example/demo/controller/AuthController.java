package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// IMPORT BARU YANG PENTING
import jakarta.servlet.http.HttpSession;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private UserService userService;

    // 👇 METHOD LOGIN YANG DIPERBAIKI 👇
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) { // <-- 1. Tambahkan HttpSession
        
        // Admin hardcoded (tetap sama)
        if ("sweetshop".equals(request.getUsername()) && "sweetshop2020".equals(request.getPassword())) {
            // Kita juga bisa set session untuk admin jika perlu
            session.setAttribute("role", "ADMIN");
            return ResponseEntity.ok("ADMIN");
        }

        // User login
        User user = userService.findByUsernameAndPassword(request.getUsername(), request.getPassword());
        
        if (user != null) {
            // --- LOGIN USER BERHASIL ---
            
            // 2. SIMPAN INFORMASI PENTING KE SESSION
            session.setAttribute("userId", user.getId()); // <-- INI KUNCINYA!
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", "USER");
            
            // 3. Kembalikan respons "USER" (sesuai kode lama agar frontend tidak error)
            return ResponseEntity.ok("USER");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }
    
    // Method register tidak perlu diubah, sudah benar
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username sudah digunakan.");
        }

        User user = new User();
        user.setNama(request.getNama());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // Disarankan untuk mengenkripsi password
        user.setNomorTelepon(request.getNomorTelepon());
        user.setAlamat(request.getAlamat());
        user.setTanggalLahir(request.getTanggalLahir());

        userService.save(user);

        return ResponseEntity.ok("Registrasi berhasil!");
    }

    // Method logout untuk menghapus session
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // Memeriksa apakah sesi ada sebelum menghapusnya
        if (session != null) {
            // Menghapus semua data dari sesi (termasuk userId, username, dll)
            session.invalidate();
        }
        // Kirim respons OK ke frontend untuk memberitahu bahwa logout di server berhasil
        return ResponseEntity.ok("Logout berhasil");
    }
}