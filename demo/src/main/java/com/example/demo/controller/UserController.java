package com.example.demo.controller;

import com.example.demo.dto.CartDto;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    // --- Services ---
    @Autowired private UserService userService;
    @Autowired private CartService cartService;

    // --- Repositories (untuk produk) ---
    @Autowired private ProductRepository productRepository;
    @Autowired private CookieRepository cookieRepo;
    @Autowired private DessertRepository dessertRepo;
    @Autowired private CelebrationCakeRepository celebrationRepo;
    @Autowired private ClassicCakeRepository classicRepo;


    // =======================================================
    // == HALAMAN-HALAMAN YANG DILIHAT USER
    // =======================================================

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        return "user_dashboard";
    }

    @GetMapping("/user/kategori/{nama}")
    public String kategoriPage(@PathVariable String nama, Model model) {
        model.addAttribute("kategori", nama);
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });

        switch (nama) {
            case "Cookie" -> model.addAttribute("produkList", cookieRepo.findAll());
            case "Dessert" -> model.addAttribute("produkList", dessertRepo.findAll());
            case "Classic Cake" -> model.addAttribute("produkList", classicRepo.findAll());
            case "Celebration Cake" -> model.addAttribute("produkList", celebrationRepo.findAll());
            default -> model.addAttribute("produkList", List.of());
        }
        return "user_kategori";
    }

    @GetMapping("/user/detail/{kategori}/{id}")
    public String detailProduk(@PathVariable String kategori, @PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/user/kategori/" + kategori;
        }
        model.addAttribute("product", product);
        model.addAttribute("kategori", kategori);
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        return "user_product_detail";
    }

    @GetMapping("/keranjang")
    public String showCartPage(Model model) {
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        return "user_cart";
    }

    @GetMapping("/profile")
    public String showEditProfileForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findUserById(userId).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        return "user_edit_profile";
    }

    @GetMapping("/riwayat-pesanan")
    public String showOrderHistoryPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        return "user_order_history";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        System.out.println("--- DEBUG LOG 1: Memasuki method showCheckoutPage ---");

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("--- DEBUG LOG ERROR: userId di session null! Mengarahkan ke login. ---");
            return "redirect:/login";
        }
        System.out.println("--- DEBUG LOG 2: Berhasil mendapatkan userId: " + userId + " ---");

        CartDto cart = cartService.getCartForUser(userId);
        System.out.println("--- DEBUG LOG 3: Berhasil mendapatkan data keranjang. Jumlah item: " + (cart.getItems() != null ? cart.getItems().size() : "null") + " ---");

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            System.out.println("--- DEBUG LOG INFO: Keranjang kosong. Mengarahkan ke halaman keranjang. ---");
            return "redirect:/keranjang";
        }

        User user = userService.findUserById(userId)
                .orElse(null); // Diubah sementara untuk tidak melempar error

        if (user == null) {
            System.out.println("--- DEBUG LOG ERROR: User dengan ID " + userId + " tidak ditemukan di database! ---");
            // Seharusnya ini tidak terjadi jika sesi valid, tapi kita cek untuk keamanan
            throw new RuntimeException("Data user tidak valid.");
        }
        System.out.println("--- DEBUG LOG 4: Berhasil mendapatkan data user: " + user.getNama() + " ---");

        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        model.addAttribute("categories", new String[] { "Celebration Cake", "Classic Cake", "Cookie", "Dessert" });
        System.out.println("--- DEBUG LOG 5: Semua data berhasil ditambahkan ke model. Siap merender halaman. ---");

        return "user_checkout";
    }

    // =======================================================
    // == AKSI DARI FORM (POST)
    // =======================================================

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UpdateProfileRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            userService.updateUserProfile(userId, request);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui profil.");
        }
        return "redirect:/profile";
    }
}