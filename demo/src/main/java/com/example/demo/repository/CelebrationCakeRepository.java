package com.example.demo.repository;

import com.example.demo.model.CelebrationCake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebrationCakeRepository extends JpaRepository<CelebrationCake, Long> {
}

