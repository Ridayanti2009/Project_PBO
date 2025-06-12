package com.example.demo.repository;

import com.example.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Method khusus untuk mencari semua transaksi milik seorang user,
     * diurutkan dari yang paling baru (Order by OrderDate Descending).
     * Ini akan sangat berguna nanti untuk halaman riwayat pesanan.
     */
    List<Transaction> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Transaction> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM Transaction t WHERE t.orderDate >= :startOfDay AND t.orderDate < :endOfDay AND t.status = 'COMPLETED'")
    double findTodaysRevenue(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    List<Transaction> findByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime startDate, LocalDateTime endDate);
}