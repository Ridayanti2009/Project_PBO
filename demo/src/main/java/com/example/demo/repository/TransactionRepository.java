package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Method khusus untuk mencari semua transaksi milik seorang user,
     * diurutkan dari yang paling baru (Order by OrderDate Descending).
     * Ini akan sangat berguna nanti untuk halaman riwayat pesanan.
     */
    List<Transaction> findByUserIdOrderByOrderDateDesc(Long userId);

}