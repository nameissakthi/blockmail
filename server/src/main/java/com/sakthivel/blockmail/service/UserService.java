package com.sakthivel.blockmail.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.sakthivel.blockmail.model.User;
import com.sakthivel.blockmail.repository.BlockchainTransactionRepository;
import com.sakthivel.blockmail.repository.EncryptedEmailRepository;
import com.sakthivel.blockmail.repository.KeyAuditLogRepository;
import com.sakthivel.blockmail.repository.QuantumKeyRepository;
import com.sakthivel.blockmail.repository.UserRepository;
import com.sakthivel.blockmail.security.SecurityConfig;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EncryptedEmailRepository encryptedEmailRepository;
    private final QuantumKeyRepository quantumKeyRepository;
    private final KeyAuditLogRepository keyAuditLogRepository;
    private final BlockchainTransactionRepository blockchainTransactionRepository;
    private final ApplicationContext context;

    public UserService(
            @Autowired UserRepository userRepository,
            @Autowired EncryptedEmailRepository encryptedEmailRepository,
            @Autowired QuantumKeyRepository quantumKeyRepository,
            @Autowired KeyAuditLogRepository keyAuditLogRepository,
            @Autowired BlockchainTransactionRepository blockchainTransactionRepository,
            @Autowired ApplicationContext context
    ) {
        this.userRepository = userRepository;
        this.encryptedEmailRepository = encryptedEmailRepository;
        this.quantumKeyRepository = quantumKeyRepository;
        this.keyAuditLogRepository = keyAuditLogRepository;
        this.blockchainTransactionRepository = blockchainTransactionRepository;
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

    public long deleteAllUsers() {
        long count = userRepository.count();
        // Clear dependent tables first to avoid foreign-key constraint violations.
        encryptedEmailRepository.deleteAll();
        keyAuditLogRepository.deleteAll();
        blockchainTransactionRepository.deleteAll();
        quantumKeyRepository.deleteAll();
        userRepository.deleteAll();
        return count;
    }
}