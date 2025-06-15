package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CheckoutRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDetail;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ProductRepository productRepository;

    // === TAMBAHAN 1: Inject UserService ===
    // Kita butuh ini untuk bisa menyimpan perubahan pada data User (alamat baru).
    @Autowired private UserService userService;
    // =======================================

    @Override
    // === PERUBAHAN 2: Ganti parameter method ini ===
    // Daripada hanya menerima 'String deliveryOption', sekarang kita menerima
    // seluruh objek 'CheckoutRequest' agar bisa mengakses alamat juga.
    public Transaction createTransaction(Long userId, CheckoutRequest request) {
    // ================================================
        User user = userRepository.findById(userId)
                // Diubah ke Bahasa Inggris
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Pengecekan keranjang yang lebih aman
        Cart cart = cartRepository.findByUserId(userId)
                // Diubah ke Bahasa Inggris
                .orElseThrow(() -> new RuntimeException("Cart for User ID: " + userId + " not found"));

        if (cart.getCartItems().isEmpty()) {
            // Diubah ke Bahasa Inggris
            throw new RuntimeException("Cart is empty. Cannot proceed to checkout.");
        }

        // Pengecekan stok & harga produk yang lebih aman
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getStok() < item.getQuantity()) {
                // Diubah ke Bahasa Inggris
                throw new RuntimeException("Stock for product '" + product.getNama() + "' is not sufficient. Remaining stock: " + product.getStok());
            }
            if (product.getHarga() == null) {
                 // Diubah ke Bahasa Inggris
                throw new RuntimeException("Price for product '" + product.getNama() + "' is not set.");
            }
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setOrderDate(LocalDateTime.now());
        transaction.setStatus("PENDING");
        // Mengambil deliveryOption dari objek request
        transaction.setDeliveryOption(request.getDeliveryOption());

        // === TAMBAHAN 3: LOGIKA UTAMA UNTUK MENYIMPAN ALAMAT ===
        // Cek jika metodenya adalah DELIVERY
        if ("DELIVERY".equalsIgnoreCase(request.getDeliveryOption())) {
            String newAddress = request.getAddress();

            // Simpan alamat ke catatan transaksi ini
            // (CATATAN: Pastikan Anda sudah menambahkan field 'shippingAddress' di model Transaction.java Anda)
            transaction.setShippingAddress(newAddress);

            // PERBARUI alamat utama di data user
            user.setAlamat(newAddress);

            // SIMPAN perubahan pada data user ke database
            userService.save(user);
        }
        // ==========================================================

        List<TransactionDetail> details = new ArrayList<>();
        double grandTotal = 0.0;

        // Loop untuk memproses item (ini sudah benar, tidak perlu diubah)
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int newStock = product.getStok() - cartItem.getQuantity();
            product.setStok(newStock);

            TransactionDetail detail = new TransactionDetail();
            detail.setProduct(product);
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(product.getHarga());
            detail.setTransaction(transaction);
            details.add(detail);
            grandTotal += (detail.getPrice() * detail.getQuantity());
        }

        // Menambahkan ongkos kirim jika ada
        if ("DELIVERY".equalsIgnoreCase(request.getDeliveryOption())) {
            grandTotal += 10000;
        }

        transaction.setTransactionDetails(details);
        transaction.setTotalAmount(grandTotal);

        Transaction savedTransaction = transactionRepository.save(transaction);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedTransaction;
    }

    // Method-method lain di bawah ini tidak perlu diubah.
    // Anda bisa salin semua method ini untuk memastikan file Anda lengkap.

    @Override
    public List<Transaction> findAllOrdersSortedByStatus() {
        List<Transaction> allOrders = transactionRepository.findAll();
        Map<String, Integer> statusOrder = new HashMap<>();
        statusOrder.put("PENDING", 1);
        statusOrder.put("PROCESSING", 2);
        statusOrder.put("SHIPPED", 3);
        statusOrder.put("COMPLETED", 4);
        statusOrder.put("CANCELLED", 5);
        allOrders.sort(Comparator.comparing(order -> {
            String status = (order.getStatus() != null) ? order.getStatus().trim().toUpperCase() : "";
            return statusOrder.getOrDefault(status, 99);
        }));
        return allOrders;
    }

    @Override
    public List<Transaction> getOrderHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
    }

    @Override
    public Transaction updateTransactionStatus(Long transactionId, String newStatus) {
        Transaction transaction = transactionRepository.findById(transactionId)
                // Diubah ke Bahasa Inggris
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        if ("COMPLETED".equalsIgnoreCase(transaction.getStatus()) || "CANCELLED".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Finalized transactions cannot be changed.");
        }
        transaction.setStatus(newStatus);
        return transactionRepository.save(transaction);
    }

    @Override
    public long countTotalOrders() {
        return transactionRepository.count();
    }

    @Override
    public double getTodaysRevenue() {
        // === INI PERBAIKANNYA ===
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 1. Ambil semua transaksi yang statusnya COMPLETED hari ini
        List<Transaction> completedOrdersToday = transactionRepository.findByStatusAndOrderDateBetween("COMPLETED", startOfDay, endOfDay);

        // 2. Jika tidak ada, kembalikan 0
        if (completedOrdersToday.isEmpty()) {
            return 0.0;
        }

        // 3. Hitung GRAND TOTAL (termasuk PPN) untuk setiap pesanan, lalu jumlahkan semuanya
        double totalRevenueWithTax = completedOrdersToday.stream()
                .mapToDouble(order -> {
                    // Logika ini meniru perhitungan di struk Anda
                    double amountFromDb = order.getTotalAmount(); // Ini adalah subtotal + ongkir

                    // Pisahkan subtotal dan ongkir untuk menghitung PPN dengan benar
                    double shippingFee = ("DELIVERY".equalsIgnoreCase(order.getDeliveryOption())) ? 10000 : 0;
                    double subtotalForTax = amountFromDb - shippingFee;
                    double taxAmount = subtotalForTax * 0.03; // PPN 3%

                    // Kembalikan total akhir untuk pesanan ini
                    return amountFromDb + taxAmount;
                })
                .sum(); // Jumlahkan semua total akhir

        return totalRevenueWithTax;
    }

    @Override
    public List<Transaction> getTodaysOrders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return transactionRepository.findByOrderDateBetween(startOfDay, endOfDay);
    }

    @Override
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                // Diubah ke Bahasa Inggris
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        if (!"PENDING".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Only orders with PENDING status can be cancelled.");
        }
        transaction.setStatus("CANCELLED");
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findTodaysRecentOrders() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX); // Akhir dari hari ini
        // Kita juga bisa tambahkan limit, misalnya 5 atau 10 transaksi terbaru
        // return transactionRepository.findByOrderDateBetweenOrderByOrderDateDesc(startOfDay, endOfDay, PageRequest.of(0, 5)).getContent();
        return transactionRepository.findByOrderDateBetweenOrderByOrderDateDesc(startOfDay, endOfDay); // Tanpa pagination untuk saat ini
    }

    @Override
    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

}
