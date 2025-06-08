package com.example.demo.repository;

import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Method khusus untuk mencari Cart berdasarkan ID dari User yang memilikinya
    Optional<Cart> findByUserId(Long userId);

}