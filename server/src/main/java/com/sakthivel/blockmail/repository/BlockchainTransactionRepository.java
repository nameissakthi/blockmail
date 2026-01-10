package com.sakthivel.blockmail.repository;

import com.sakthivel.blockmail.model.BlockchainTransaction;
import com.sakthivel.blockmail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainTransactionRepository extends JpaRepository<BlockchainTransaction, String> {

    Optional<BlockchainTransaction> findByTransactionHash(String transactionHash);

    List<BlockchainTransaction> findByReferenceId(String referenceId);

    List<BlockchainTransaction> findByUserOrderByTimestampDesc(User user);

    List<BlockchainTransaction> findByTransactionType(String transactionType);

    List<BlockchainTransaction> findByVerified(Boolean verified);

    List<BlockchainTransaction> findByBlockchainNetwork(String network);
}

