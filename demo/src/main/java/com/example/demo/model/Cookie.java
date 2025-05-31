package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cookies")
public class Cookie extends Product {

    private String ukuran;

    public Cookie() {}

    public Cookie(String nama, String deskripsi, Double harga, String gambarUrl, int stok,String ukuran) {
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
