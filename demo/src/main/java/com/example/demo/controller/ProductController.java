package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CelebrationCakeRepository celebrationCakeRepository;

    @Autowired
    private ClassicCakeRepository classicCakeRepository;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private DessertRepository dessertRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // 👇 Menampilkan semua produk
    @GetMapping("/products")
    public String productPage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // templates/products.html
    }

    // 👇 Proses tambah produk
    @PostMapping("/products/add")
    public String addProduct(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("price") double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "stock", required = false, defaultValue = "0") int stock,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "ukuran", required = false) String ukuran,
            @RequestParam(value = "varian", required = false) String varian,
            Model model
    ) {
        String imageUrl = null;

        try {
            imageUrl = saveImage(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Gagal upload gambar");
            return "error";
        }

        switch (category) {
            case "celebrationcake":
                CelebrationCake cc = new CelebrationCake(name, description, price, imageUrl, stock, ukuran);
                celebrationCakeRepository.save(cc);
                break;
            case "cookie":
                Cookie cookie = new Cookie(name, description, price, imageUrl, stock, ukuran);
                cookieRepository.save(cookie);
                break;
            case "dessert":
                Dessert dessert = new Dessert(name, description, price, imageUrl, stock, varian);
                dessertRepository.save(dessert);
                break;
            case "classiccake":
                ClassicCake classic = new ClassicCake(name, description, price, imageUrl, stock, varian);
                classicCakeRepository.save(classic);
                break;
            default:
                model.addAttribute("error", "Kategori tidak valid");
                return "error";
        }

        return "redirect:/products";
    }

    // 👇 Menampilkan berdasarkan kategori
    @GetMapping("/products/category/{category}")
    public String getProductsByCategory(@PathVariable String category, Model model) {
        switch (category) {
            case "celebrationcake":
                model.addAttribute("products", celebrationCakeRepository.findAll());
                break;
            case "cookie":
                model.addAttribute("products", cookieRepository.findAll());
                break;
            case "dessert":
                model.addAttribute("products", dessertRepository.findAll());
                break;
            case "classiccake":
                model.addAttribute("products", classicCakeRepository.findAll());
                break;
            default:
                model.addAttribute("error", "Kategori tidak ditemukan");
                return "error";
        }
        model.addAttribute("category", category);
        return "category-products"; // kamu bisa bikin view ini nanti
    }

    // 👇 Helper method simpan file
    public String saveImage(MultipartFile gambarFile) throws IOException {
    if (gambarFile != null && !gambarFile.isEmpty()) {
        String uploadDir = new File("uploads").getAbsolutePath(); // Ubah relative path jadi absolut
        Files.createDirectories(Paths.get(uploadDir)); // Pastikan folder ada

        String filename = UUID.randomUUID().toString() + "_" + gambarFile.getOriginalFilename();
        File dest = new File(uploadDir + File.separator + filename); // Gunakan path absolut

        System.out.println("Upload folder: " + uploadDir);
        System.out.println("Save filename: " + filename);
        System.out.println("Menyimpan file di: " + dest.getAbsolutePath());

        gambarFile.transferTo(dest); // Simpan file ke path absolut

        return filename;
    }
    return null;
}


}
