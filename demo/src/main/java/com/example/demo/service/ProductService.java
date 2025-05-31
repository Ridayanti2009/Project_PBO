package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.CelebrationCakeRepository;
import com.example.demo.repository.ClassicCakeRepository;
import com.example.demo.repository.CookieRepository;
import com.example.demo.repository.DessertRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Service
public class ProductService {

    @Autowired
    private CelebrationCakeRepository celebrationCakeRepo;

    @Autowired
    private ClassicCakeRepository classicCakeRepo;

    @Autowired
    private CookieRepository cookieRepo;

    @Autowired
    private DessertRepository dessertRepo;

    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(celebrationCakeRepo.findAll());
        allProducts.addAll(classicCakeRepo.findAll());
        allProducts.addAll(cookieRepo.findAll());
        allProducts.addAll(dessertRepo.findAll());
        return allProducts;
    }
}


