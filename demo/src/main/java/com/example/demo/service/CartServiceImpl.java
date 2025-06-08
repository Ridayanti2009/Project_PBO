package com.example.demo.service;

import com.example.demo.dto.CartDto;
import com.example.demo.dto.CartItemDto;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;

    @Override
    public void addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> cartRepository.save(new Cart(user)));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public CartDto getCartForUser(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());

        List<CartItemDto> itemDtos = cart.getCartItems().stream()
                .map(this::convertToDto)
                .filter(Objects::nonNull) // <-- PERBAIKAN: Hapus item jika datanya tidak valid
                .collect(Collectors.toList());

        double grandTotal = itemDtos.stream()
                .mapToDouble(CartItemDto::getSubtotal)
                .sum();

        CartDto cartDto = new CartDto();
        cartDto.setItems(itemDtos);
        cartDto.setGrandTotal(grandTotal);

        return cartDto;
    }

    // --- Method Helper yang Diperbaiki ---
    private CartItemDto convertToDto(CartItem cartItem) {
        Product product = cartItem.getProduct();
        if (product == null) {
            return null; // Pengaman jika produk tidak ada
        }

        CartItemDto dto = new CartItemDto();
        dto.setCartItemId(cartItem.getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getNama());
        
        // --- PERBAIKAN UTAMA: Safety Check untuk Harga ---
        double price = (product.getHarga() != null) ? product.getHarga() : 0.0;
        dto.setPrice(price);
        
        dto.setQuantity(cartItem.getQuantity());
        dto.setImageUrl(product.getGambarUrl());
        
        // Hitung subtotal dengan harga yang sudah aman
        dto.setSubtotal(price * cartItem.getQuantity());

        return dto;
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item dengan ID " + cartItemId + " tidak ditemukan"));
        Cart cart = cartItem.getCart();
        if (cart != null) {
            cart.getCartItems().remove(cartItem);
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void updateItemQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            removeItemFromCart(cartItemId);
        } else {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("CartItem not found"));
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }
}