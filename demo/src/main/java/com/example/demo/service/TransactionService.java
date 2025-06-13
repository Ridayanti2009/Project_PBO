package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CheckoutRequest;
import com.example.demo.model.Transaction;

public interface TransactionService {

    /**
     * Membuat transaksi baru dari keranjang user. Ini adalah inti dari proses checkout.
     */
    Transaction createTransaction(Long userId, CheckoutRequest request);

    /**
     * Mengambil riwayat pesanan untuk seorang user.
     */
    List<Transaction> getOrderHistory(Long userId);

    /**
     * Mengambil semua transaksi untuk ditampilkan di halaman admin.
     */
    List<Transaction> getAllTransactions();

    /**
     * Mengupdate status sebuah transaksi (untuk admin).
     */
    Transaction updateTransactionStatus(Long transactionId, String newStatus);

    /**
     * Mengambil semua transaksi yang diurutkan berdasarkan status (untuk admin).
     */
    List<Transaction> findAllOrdersSortedByStatus();

    void cancelTransaction(Long transactionId);

    /**
     * Untuk dashboard Admin).
     */
    long countTotalOrders();
    double getTodaysRevenue();
    List<Transaction> getTodaysOrders();
    List<Transaction> findTodaysRecentOrders();
    Transaction findById(Long id);


}