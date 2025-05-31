package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "desserts")
public class Dessert extends Product {

    private String varian;

    public Dessert() {}

    public Dessert(String nama, String deskripsi, Double harga, String gambarUrl, int stok, String varian) {
        super(nama, deskripsi, harga, gambarUrl, stok);
        this.varian = varian;
    }

    public String getVarian() {
        return varian;
    }

    public void setVarian(String varian) {
        this.varian = varian;
    }
}
