package com.sakthivel.blockmail.service.crypto;

import com.sakthivel.blockmail.model.SecurityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting appropriate cryptography service based on security level
 */
@Component
public class CryptographyServiceFactory {

    private final OtpCryptographyService otpService;
    private final QuantumAidedAesCryptographyService quantumAesService;
    private final PqcCryptographyService pqcService;
    private final StandardCryptographyService standardService;

    public CryptographyServiceFactory(
            @Autowired OtpCryptographyService otpService,
            @Autowired QuantumAidedAesCryptographyService quantumAesService,
            @Autowired PqcCryptographyService pqcService,
            @Autowired StandardCryptographyService standardService) {
        this.otpService = otpService;
        this.quantumAesService = quantumAesService;
        this.pqcService = pqcService;
        this.standardService = standardService;
    }

    /**
     * Get appropriate cryptography service for given security level
     */
    public CryptographyService getService(SecurityLevel securityLevel) {
        return switch (securityLevel) {
            case QUANTUM_SECURE_OTP -> otpService;
            case QUANTUM_AIDED_AES -> quantumAesService;
            case POST_QUANTUM_CRYPTO -> pqcService;
            case STANDARD_ENCRYPTION -> standardService;
        };
    }
}

