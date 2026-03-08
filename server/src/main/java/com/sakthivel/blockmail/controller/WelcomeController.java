package com.sakthivel.blockmail.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Quantum Mail Backend");
        response.put("version", "v1");
        response.put("status", "running");
        response.put("message", "Welcome to Quantum Mail Secure Communication System");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("register", "POST /register");
        endpoints.put("login", "POST /login");
        endpoints.put("health", "GET /actuator/health");
        endpoints.put("documentation", "See README.md for full API documentation");

        response.put("public_endpoints", endpoints);
        response.put("features", new String[]{
            "Quantum Key Distribution (QKD)",
            "Multi-level Encryption",
            "Blockchain Integration",
            "Secure Email Communication"
        });

        return response;
    }
}

