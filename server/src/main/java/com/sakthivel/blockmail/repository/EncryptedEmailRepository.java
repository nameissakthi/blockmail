package com.sakthivel.blockmail.repository;

import com.sakthivel.blockmail.model.EncryptedEmail;
import com.sakthivel.blockmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EncryptedEmailRepository extends JpaRepository<EncryptedEmail, String> {

    List<EncryptedEmail> findBySenderOrderBySentAtDesc(User sender);

    List<EncryptedEmail> findByRecipientEmailOrderBySentAtDesc(String recipientEmail);

    List<EncryptedEmail> findBySenderAndRecipientEmail(User sender, String recipientEmail);

    List<EncryptedEmail> findBySentAtBetween(LocalDateTime start, LocalDateTime end);

    List<EncryptedEmail> findByBlockchainTxHashIsNotNull();
}

