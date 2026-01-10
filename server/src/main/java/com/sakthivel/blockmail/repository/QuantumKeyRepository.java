package com.sakthivel.blockmail.repository;

import com.sakthivel.blockmail.model.KeyStatus;
import com.sakthivel.blockmail.model.QuantumKey;
import com.sakthivel.blockmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuantumKeyRepository extends JpaRepository<QuantumKey, String> {

    Optional<QuantumKey> findByKeyId(String keyId);

    List<QuantumKey> findByOwnerAndStatus(User owner, KeyStatus status);

    List<QuantumKey> findByOwner(User owner);

    List<QuantumKey> findByStatusAndExpiresAtBefore(KeyStatus status, LocalDateTime dateTime);

    long countByOwnerAndStatus(User owner, KeyStatus status);

    Optional<QuantumKey> findFirstByOwnerAndStatusOrderByObtainedAtAsc(User owner, KeyStatus status);
}
