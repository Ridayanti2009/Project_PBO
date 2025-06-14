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

    @GetMapping("/products/category/{category}")
    public String getProductsByCategory(@PathVariable String category, Model model) {
        switch (category) {
            case "celebrationcake":
                model.addAttribute("products", celebrationCakeRepository.findAll());
                return "celebrationcake";
            case "cookie":
                model.addAttribute("products", cookieRepository.findAll());
                return "cookie";
            case "dessert":
                model.addAttribute("products", dessertRepository.findAll());
                return "dessert";
            case "classiccake":
                model.addAttribute("products", classicCakeRepository.findAll());
                return "classiccake";
            default:
                model.addAttribute("error", "Kategori tidak ditemukan");
                return "error";
        }
    }

    @GetMapping("/products/detail/{kategori}/{id}")
    public String detailProduk(@PathVariable String kategori, @PathVariable Long id, Model model) {
        Object produk = null;

        switch (kategori.toLowerCase()) {
            case "cookie" -> produk = cookieRepository.findById(id).orElse(null);
            case "dessert" -> produk = dessertRepository.findById(id).orElse(null);
            case "classiccake" -> produk = classicCakeRepository.findById(id).orElse(null);
            case "celebrationcake" -> produk = celebrationCakeRepository.findById(id).orElse(null);
        }

        if (produk == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", produk);
        model.addAttribute("kategori", kategori);
        return "detail_produk";
    }

    @GetMapping("/products/delete/{kategori}/{id}")
    public String deleteProduct(@PathVariable String kategori, @PathVariable Long id) {
        switch (kategori.toLowerCase()) {
            case "cookie" -> cookieRepository.deleteById(id);
            case "dessert" -> dessertRepository.deleteById(id);
            case "classiccake" -> classicCakeRepository.deleteById(id);
            case "celebrationcake" -> celebrationCakeRepository.deleteById(id);
        }
        return "redirect:/products";
    }


    @PostMapping("/products/edit/{kategori}/{id}")
    public String updateProduct(
            @PathVariable String kategori,
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "0") int stock,
            @RequestParam(required = false) String ukuran,
            @RequestParam(required = false) String varian
    ) {
        switch (kategori.toLowerCase()) {
            case "celebrationcake" -> {
                CelebrationCake cc = celebrationCakeRepository.findById(id).orElse(null);
                if (cc != null) {
                    cc.setNama(name);
                    cc.setHarga(price);
                    cc.setDeskripsi(description);
                    cc.setStok(stock);
                    cc.setUkuran(ukuran);
                    celebrationCakeRepository.save(cc);
                }
            }
            case "cookie" -> {
                Cookie ck = cookieRepository.findById(id).orElse(null);
                if (ck != null) {
                    ck.setNama(name);
                    ck.setHarga(price);
                    ck.setDeskripsi(description);
                    ck.setStok(stock);
                    ck.setUkuran(ukuran);
                    cookieRepository.save(ck);
                }
            }
            case "dessert" -> {
                Dessert d = dessertRepository.findById(id).orElse(null);
                if (d != null) {
                    d.setNama(name);
                    d.setHarga(price);
                    d.setDeskripsi(description);
                    d.setStok(stock);
                    d.setVarian(varian);
                    dessertRepository.save(d);
                }
            }
            case "classiccake" -> {
                ClassicCake cc = classicCakeRepository.findById(id).orElse(null);
                if (cc != null) {
                    cc.setNama(name);
                    cc.setHarga(price);
                    cc.setDeskripsi(description);
                    cc.setStok(stock);
                    cc.setVarian(varian);
                    classicCakeRepository.save(cc);
                }
            }
        }

        return "redirect:/products/detail/" + kategori + "/" + id;
    }

    @GetMapping("/products/edit/{kategori}/{id}")
    public String showEditForm(@PathVariable String kategori, @PathVariable Long id, Model model) {
        Object produk = switch (kategori.toLowerCase()) {
            case "cookie" -> cookieRepository.findById(id).orElse(null);
            case "dessert" -> dessertRepository.findById(id).orElse(null);
            case "classiccake" -> classicCakeRepository.findById(id).orElse(null);
            case "celebrationcake" -> celebrationCakeRepository.findById(id).orElse(null);
            default -> null;
        };

        if (produk == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", produk);
        model.addAttribute("kategori", kategori);
        return "edit_produk";
    }
}
