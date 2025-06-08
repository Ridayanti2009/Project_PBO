// src/main/java/com/example/demo/service/TransactionServiceImpl.java
package com.example.demo.service;

// ===============================================
// == PASTIKAN BAGIAN IMPORT SUDAH SEPERTI INI ==
// ===============================================
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product; // Pastikan ini dari 'model', bukan 'service'
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDetail;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ProductRepository productRepository; 

    @Override
    public Transaction createTransaction(Long userId, String deliveryOption) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan dengan ID: " + userId));
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Keranjang untuk User ID: " + userId + " tidak ditemukan"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Keranjang belanja kosong. Tidak bisa checkout.");
        }

        // Loop pertama: hanya untuk mengecek stok
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (product.getStok() < item.getQuantity()) {
                throw new RuntimeException("Stok untuk produk '" + product.getNama() + "' tidak mencukupi. Sisa stok: " + product.getStok());
            }
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setOrderDate(LocalDateTime.now());
        transaction.setStatus("DIPROSES");
        transaction.setDeliveryOption(deliveryOption);

        List<TransactionDetail> details = new ArrayList<>();
        double grandTotal = 0.0;

        // Loop kedua: untuk memproses dan mengurangi stok
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct(); // <-- Variabel ini sekarang tipenya sudah benar

            // Kurangi stok
            int newStock = product.getStok() - cartItem.getQuantity();
            product.setStok(newStock);

            // Buat detail transaksi
            TransactionDetail detail = new TransactionDetail();
            detail.setProduct(product); // <-- Panggilan ini sekarang sudah benar
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(product.getHarga()); // <-- Panggilan ini sekarang sudah benar
            detail.setTransaction(transaction);
            details.add(detail);
            
            grandTotal += (detail.getPrice() * detail.getQuantity());
        }
        
        transaction.setTransactionDetails(details);
        transaction.setTotalAmount(grandTotal);

        Transaction savedTransaction = transactionRepository.save(transaction);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedTransaction;
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
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan dengan ID: " + transactionId));

        if ("SELESAI".equalsIgnoreCase(transaction.getStatus()) || "DIBATALKAN".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Transaksi yang sudah final tidak dapat diubah statusnya.");
        }

        transaction.setStatus(newStatus);
        return transactionRepository.save(transaction);
    }
}
