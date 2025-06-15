package com.example.demo.dto;

public class CheckoutResponse {
    private Long transactionId;
    private String message;

    public CheckoutResponse(Long transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
    }

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