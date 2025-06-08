// src/main/java/com/example/demo/dto/AddToCartRequest.java
package com.example.demo.dto;

public class AddToCartRequest {
    private Long productId;
    private int quantity;

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}