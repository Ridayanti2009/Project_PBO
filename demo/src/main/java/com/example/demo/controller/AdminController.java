package com.example.demo.controller;

// Tambahkan import-import yang dibutuhkan
import com.example.demo.dto.StatusUpdateRequest;
import com.example.demo.model.Transaction;
import com.example.demo.service.ProductService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// jika file baru
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
public class AdminController {

    @Autowired private TransactionService transactionService;
    @Autowired private UserService userService;
    @Autowired private ProductService productService;


    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        // Ambil data yang diperlukan untuk dashboard
         // 1. Ambil semua data statistik dari service
        long totalOrders = transactionService.countTotalOrders();
        double todaysRevenue = transactionService.getTodaysRevenue();
        long totalUsers = userService.countTotalUsers();
        long totalProducts = productService.countTotalProducts();
        List<Transaction> todaysOrders = transactionService.getTodaysOrders();

        // 2. Kirim semua data ke halaman HTML
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("todaysRevenue", todaysRevenue);
         // Tambahkan data lainnya ke model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("todaysOrders", todaysOrders);
        model.addAttribute("recentOrders", transactionService.getAllTransactions());
        return "admin-dashboard";
    }

    /**
     * 2. Method untuk MENAMPILKAN halaman daftar pesanan (orders) untuk admin.
     * Ini yang akan dipanggil saat admin klik menu /admin/orders.
     */
    @GetMapping("/admin/orders")
    public String showOrdersPage(Model model) {
        List<Transaction> allTransactions = transactionService.findAllOrdersSortedByStatus();
        model.addAttribute("orders", allTransactions);
        return "admin_orders"; // Nama file HTML yang akan kita buat nanti
    }

    /**
     * 3. Method API untuk MENGUPDATE status pesanan.
     * Ini yang akan dipanggil oleh JavaScript dari halaman admin.
     */
    @PostMapping("/admin/api/orders/{transactionId}/status")
    @ResponseBody // Penting! Agar method ini mengembalikan data (JSON), bukan nama halaman HTML
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long transactionId,
                                                @RequestBody StatusUpdateRequest request) {
        try {
            transactionService.updateTransactionStatus(transactionId, request.getStatus());
            return ResponseEntity.ok().build(); // Kirim status 200 OK jika berhasil
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            transactionService.cancelTransaction(orderId);
            return ResponseEntity.ok().build(); // Kirim status 200 OK
        } catch (RuntimeException e) {
            // Jika ada error, kirim pesan errornya
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}