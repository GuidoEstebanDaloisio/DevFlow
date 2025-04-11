package com.example.DevFlow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // Spring busca en /templates/index.html
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Carga templates/login.html
    }
}