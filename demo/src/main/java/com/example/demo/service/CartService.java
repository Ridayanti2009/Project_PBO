package com.example.demo.service;

import com.example.demo.dto.CartDto;

public interface CartService {
    void addToCart(Long userId, Long productId, int quantity);
    CartDto getCartForUser(Long userId);
    void removeItemFromCart(Long cartItemId);
    void updateItemQuantity(Long cartItemId, int quantity);
}