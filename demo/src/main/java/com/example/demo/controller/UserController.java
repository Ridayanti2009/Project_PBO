package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        model.addAttribute("categories", new String[] {
            "Celebration Cake", "Classic Cake", "Cookie", "Dessert"
        });
        return "user_dashboard";
    }

    @GetMapping("/user/kategori/{nama}")
    public String kategoriPage(@PathVariable String nama, Model model) {
        model.addAttribute("kategori", nama);

        List<Produk> produkList = new ArrayList<>();
        produkList.add(new Produk("Rainbow Cake", 150000, "/images/rainbow.jpg", 10));
        produkList.add(new Produk("Choco Cake", 130000, "/images/choco.jpg", 5));
        model.addAttribute("produkList", produkList);

        return "kategori";
    }

    public static class Produk {
        public String nama;
        public int harga;
        public String gambar;
        public int stok;

        public Produk(String nama, int harga, String gambar, int stok) {
            this.nama = nama;
            this.harga = harga;
            this.gambar = gambar;
            this.stok = stok;
        }

        // Getter
        public String getNama() { return nama; }
        public int getHarga() { return harga; }
        public String getGambar() { return gambar; }
        public int getStok() { return stok; }
    }
}
