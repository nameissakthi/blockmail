package com.sakthivel.blockmail.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

/**
 * Blockchain Configuration for Web3j
 * Connects to Ganache or other Ethereum-compatible networks
 */
@Configuration
@Slf4j
public class BlockchainConfig {

    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;

    @Value("${blockchain.rpc.url:http://127.0.0.1:7545}")
    private String rpcUrl;

    @Value("${blockchain.contract.address:}")
    private String contractAddress;

    @Value("${blockchain.chain.id:1337}")
    private long chainId;

    @Bean
    public Web3j web3j() {
        if (!blockchainEnabled) {
            log.warn("⚠️ Blockchain integration is DISABLED. Using mock mode.");
            return null;
        }

        try {
            log.info("🔗 Connecting to blockchain at: {}", rpcUrl);
            Web3j web3j = Web3j.build(new HttpService(rpcUrl));

            // Test connection
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.info("✅ Connected to blockchain: {}", clientVersion);
            log.info("📍 Contract address: {}", contractAddress);
            log.info("🔢 Chain ID: {}", chainId);

            return web3j;
        } catch (Exception e) {
            log.error("❌ Failed to connect to blockchain: {}", e.getMessage());
            log.warn("⚠️ Falling back to mock mode");
            return null;
        }
    }

    @Bean
    public DefaultGasProvider gasProvider() {
        return new DefaultGasProvider();
    }

    public boolean isBlockchainEnabled() {
        return blockchainEnabled;
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public long getChainId() {
        return chainId;
    }
}

