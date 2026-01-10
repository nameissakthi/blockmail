package com.sakthivel.blockmail.controller;

import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.security.JwtUtil;
import com.sakthivel.blockmail.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public String login(@RequestBody User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        return jwtUtil.generateToken(user.getEmail());
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.addNewUser(user);
    }
}