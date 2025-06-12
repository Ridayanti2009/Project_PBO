package com.example.demo.controller;

// Pastikan import DTO yang benar
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.dto.CheckoutResponse; // <-- Ganti dari StatusUpdateRequest
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Endpoint untuk user melakukan checkout.
     * URL: POST /api/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> performCheckout(@RequestBody CheckoutRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anda harus login untuk checkout.");
        }

        try {
            Transaction newTransaction = transactionService.createTransaction(userId, request);
            String message = "Transaction successful! Your Transaction ID:" + newTransaction.getId();


            CheckoutResponse response = new CheckoutResponse(newTransaction.getId(), message);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint untuk user melihat riwayat pesanannya sendiri.
     * URL: GET /api/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Transaction>> getOrderHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Transaction> orders = transactionService.getOrderHistory(userId);
        return ResponseEntity.ok(orders);
    }
}
