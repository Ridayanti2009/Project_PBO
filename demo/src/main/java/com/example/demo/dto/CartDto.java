package com.example.demo.dto;

import java.util.List;

public class CartDto {
    private List<CartItemDto> items;
    private double grandTotal;

    // Getters and Setters
    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
}