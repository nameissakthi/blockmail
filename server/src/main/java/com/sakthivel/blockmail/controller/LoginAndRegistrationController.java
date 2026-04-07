package com.sakthivel.blockmail.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.security.JwtUtil;
import com.sakthivel.blockmail.service.UserService;

@RestController
public class LoginAndRegistrationController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public LoginAndRegistrationController(
            @Autowired JwtUtil jwtUtil,
            @Autowired AuthenticationManager authenticationManager,
            @Autowired UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );

            return ResponseEntity.ok(jwtUtil.generateToken(user.getEmail()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Invalid email or password"
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Authentication failed"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.addNewUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}