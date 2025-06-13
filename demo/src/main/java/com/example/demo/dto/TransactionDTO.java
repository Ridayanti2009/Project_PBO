package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Product;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionDetail;

public class TransactionDTO {

    private Long id;
    private String status;
    private String deliveryOption;
    private double totalAmount;
    private LocalDateTime orderDate;


    private List<ItemDTO> transactionDetails;

    // Konstruktor dari entity Transaction
    public TransactionDTO(Transaction trx) {
        this.id = trx.getId();
        this.status = trx.getStatus();
        this.deliveryOption = trx.getDeliveryOption();
        this.totalAmount = trx.getTotalAmount();
        this.orderDate = trx.getOrderDate();
        this.transactionDetails = new ArrayList<>();

        if (trx.getTransactionDetails() != null) {
            for (TransactionDetail detail : trx.getTransactionDetails()) {
                Product product = detail.getProduct();
                String productName = product != null ? product.getNama() : "Unknown";

                ItemDTO item = new ItemDTO(
                    detail.getQuantity(),
                    detail.getPrice(),
                    productName
                );
                this.transactionDetails.add(item);
            }
        }
    }

    // Getter semua field
    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public List<ItemDTO> getTransactionDetails() {
        return transactionDetails;
    }

    // Inner class ItemDTO
    public static class ItemDTO {
        private int quantity;
        private double price;
        private String productName;

        public ItemDTO(int quantity, double price, String productName) {
            this.quantity = quantity;
            this.price = price;
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public String getProductName() {
            return productName;
        }
    }
}
