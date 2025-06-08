// src/main/java/com/example/demo/controller/AdminController.java
package com.example.demo.controller;

// Tambahkan import-import yang dibutuhkan
import com.example.demo.dto.StatusUpdateRequest;
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class AdminController {

    // === BAGIAN YANG SUDAH ADA ===
    // (Note: saya ganti nama return value agar konsisten dengan file yg akan kita buat nanti)
    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        // Nanti di sini bisa kamu tambahkan logika untuk mengambil data
        // total produk, total order, dll. untuk ditampilkan di dashboard
        return "admin-dashboard"; 
    }

    // === BAGIAN BARU YANG DITAMBAHKAN ===

    // 1. Inject TransactionService agar bisa kita gunakan
    @Autowired
    private TransactionService transactionService;

    /**
     * 2. Method untuk MENAMPILKAN halaman daftar pesanan (orders) untuk admin.
     * Ini yang akan dipanggil saat admin klik menu /admin/orders.
     */
    @GetMapping("/admin/orders")
    public String showOrdersPage(Model model) {
        List<Transaction> allTransactions = transactionService.getAllTransactions();
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
}