package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "celebration_cakes")
@PrimaryKeyJoinColumn(name = "product_id")
public class CelebrationCake extends Product {

    private String ukuran;

    public CelebrationCake() {}

    public CelebrationCake(String nama, String deskripsi, Double harga, String gambarUrl, int stok, String ukuran) {
        super(nama, deskripsi, harga, gambarUrl, stok);
        this.ukuran = ukuran;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }
}
