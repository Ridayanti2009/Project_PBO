// src/main/java/com/example/demo/dto/CheckoutResponse.java
package com.example.demo.dto;

public class CheckoutResponse {
    private Long transactionId;
    private String message;

    // Constructor untuk memudahkan membuat objeknya di Controller
    public CheckoutResponse(Long transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}