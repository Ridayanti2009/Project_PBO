package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "celebration_cakes")
public class CelebrationCake extends Product {

    private String ukuran;

    public CelebrationCake() {}

    public CelebrationCake(String nama, String deskripsi, Double harga, String gambarUrl, String ukuran) {
        super(nama, deskripsi, harga, gambarUrl);
        this.ukuran = ukuran;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }
}
