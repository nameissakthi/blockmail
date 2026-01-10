package com.sakthivel.blockmail.controller;
import com.sakthivel.blockmail.dto.BlockchainVerificationDTO;
import com.sakthivel.blockmail.model.BlockchainTransaction;
import com.sakthivel.blockmail.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/blockchain")
@Slf4j
public class BlockchainVerificationController {
    private final BlockchainService blockchainService;
    public BlockchainVerificationController(@Autowired BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }
    @GetMapping("/verify/{transactionHash}")
    public ResponseEntity<?> verifyTransaction(@PathVariable String transactionHash) {
        try {
            boolean verified = blockchainService.verifyTransaction(transactionHash);
            BlockchainTransaction transaction = blockchainService.getTransaction(transactionHash);
            BlockchainVerificationDTO dto = new BlockchainVerificationDTO();
            dto.setTransactionHash(transaction.getTransactionHash());
            dto.setReferenceId(transaction.getReferenceId());
            dto.setDataHash(transaction.getDataHash());
            dto.setVerified(verified);
            dto.setBlockNumber(transaction.getBlockNumber());
            dto.setNetwork(transaction.getBlockchainNetwork());
            dto.setTimestamp(transaction.getTimestamp().toString());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "verification", dto
            ));
        } catch (Exception e) {
            log.error("Failed to verify transaction", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Verification failed: " + e.getMessage()
            ));
        }
    }
    @GetMapping("/transaction/{transactionHash}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionHash) {
        try {
            BlockchainTransaction transaction = blockchainService.getTransaction(transactionHash);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "transaction", Map.of(
                            "hash", transaction.getTransactionHash(),
                            "type", transaction.getTransactionType(),
                            "referenceId", transaction.getReferenceId(),
                            "dataHash", transaction.getDataHash(),
                            "verified", transaction.getVerified(),
                            "blockNumber", transaction.getBlockNumber(),
                            "network", transaction.getBlockchainNetwork(),
                            "timestamp", transaction.getTimestamp().toString()
                    )
            ));
        } catch (Exception e) {
            log.error("Failed to get transaction", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
