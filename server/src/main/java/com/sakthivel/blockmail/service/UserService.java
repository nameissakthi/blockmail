package com.sakthivel.blockmail.service;

import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.repository.UserRepository;
import com.sakthivel.blockmail.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationContext context;

    public UserService(@Autowired UserRepository userRepository, @Autowired ApplicationContext context) {
        this.userRepository = userRepository;
        this.context = context;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }
        return user;
    }

    public String addNewUser(User user) {
        // Check if user already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        user.setPassword(context.getBean(SecurityConfig.class).getPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }
}