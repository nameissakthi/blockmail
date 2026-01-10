package com.sakthivel.blockmail.repository;

import com.sakthivel.blockmail.model.KeyAuditLog;
import com.sakthivel.blockmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KeyAuditLogRepository extends JpaRepository<KeyAuditLog, String> {

    List<KeyAuditLog> findByQuantumKeyIdOrderByTimestampDesc(String quantumKeyId);

    List<KeyAuditLog> findByUserOrderByTimestampDesc(User user);

    List<KeyAuditLog> findByEmailId(String emailId);

    List<KeyAuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<KeyAuditLog> findByBlockchainVerified(Boolean verified);
}

