package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // admin hardcoded
        if ("sweetshop".equals(request.getUsername()) && "sweetshop2020".equals(request.getPassword())) {
            return ResponseEntity.ok("ADMIN");
        }

        // user login
        User user = userService.findByUsernameAndPassword(request.getUsername(), request.getPassword());
        if (user != null) {
            return ResponseEntity.ok("USER");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("📅 Tanggal Lahir dari Request: " + request.getTanggalLahir());

        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username sudah digunakan.");
        }

        User user = new User();
        user.setNama(request.getNama());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNomorTelepon(request.getNomorTelepon());
        user.setAlamat(request.getAlamat());
        user.setTanggalLahir(request.getTanggalLahir());

        userService.save(user);

        return ResponseEntity.ok("Registrasi berhasil!");
    }

}
