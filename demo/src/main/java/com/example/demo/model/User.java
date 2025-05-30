package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nama;
    private String email;
    private String username;
    private String password;
    private String nomorTelepon;
    private String alamat;
    private LocalDate tanggalLahir;

    // Constructor kosong
    public User() {}

    // Constructor dengan parameter
    public User(String nama, String email, String username, String password, String nomorTelepon, String alamat,  LocalDate tanggalLahir) {
        this.nama = nama;
        this.email = email;
        this.username = username;
        this.password = password;
        this.nomorTelepon = nomorTelepon;
        this.alamat = alamat;
        this.tanggalLahir = tanggalLahir;
    }

    // Getters dan Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

}
