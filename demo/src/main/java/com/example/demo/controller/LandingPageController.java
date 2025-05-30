package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    @GetMapping("/")
    public String landingPage() {
        return "index"; // src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // src/main/resources/templates/login.html
    }

    @GetMapping("/registrasi")
    public String registrasiPage() {
        return "registrasi"; // src/main/resources/templates/registrasi.html
    }
}
