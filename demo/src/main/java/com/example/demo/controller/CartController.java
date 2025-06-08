package com.example.demo.controller;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.dto.CartDto;
import com.example.demo.dto.UpdateQuantityRequest;
import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart") // Semua URL di controller ini akan diawali dengan /api/cart
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Endpoint untuk menambahkan item ke keranjang.
     * URL: POST /api/cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Silakan login terlebih dahulu.");
        }

        try {
            cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Produk berhasil ditambahkan ke keranjang.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint untuk mendapatkan isi keranjang user yang sedang login.
     * URL: GET /api/cart
     */
    @GetMapping
    public ResponseEntity<CartDto> getCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // Jika tidak login, kita bisa mengembalikan keranjang kosong atau error
            // Di sini kita pilih kembalikan error.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CartDto cartDto = cartService.getCartForUser(userId);
        return ResponseEntity.ok(cartDto);
    }

    /**
     * Endpoint untuk menghapus item dari keranjang.
     * URL: DELETE /api/cart/item/{cartItemId}
     */
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartItemId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Silakan login terlebih dahulu.");
        }
        
        try {
            cartService.removeItemFromCart(cartItemId);
            return ResponseEntity.ok("Item berhasil dihapus dari keranjang.");
        } catch (Exception e) {
            // Ini akan menangani jika item dengan ID tersebut tidak ditemukan
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long cartItemId, 
                                                @RequestBody UpdateQuantityRequest request,
                                                HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Silakan login terlebih dahulu.");
        }

        try {
            cartService.updateItemQuantity(cartItemId, request.getQuantity());
            return ResponseEntity.ok("Jumlah item berhasil diperbarui.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}