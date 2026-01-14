// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

/**
 * @title QuantumMailRegistry
 * @dev Smart contract for registering and verifying quantum-secured emails on Ethereum blockchain
 * @notice This contract stores email verification hashes and quantum key IDs on-chain
 */
contract QuantumMailRegistry {

    // Email record structure
    struct EmailRecord {
        bytes32 emailHash;          // SHA-256 hash of encrypted email content
        bytes32 qkdKeyId;           // Quantum Key Distribution key identifier
        address sender;             // Ethereum address of sender
        address recipient;          // Ethereum address of recipient (optional)
        uint256 timestamp;          // Block timestamp when registered
        string securityLevel;       // Security level: OTP, AES, PQC, STANDARD
        bool verified;              // Verification status
        string metadata;            // Additional metadata (IPFS hash, etc.)
    }

    // Quantum key audit record
    struct KeyAuditRecord {
        bytes32 keyId;              // QKD key identifier
        address user;               // User who used the key
        string action;              // Action: OBTAINED, ACTIVATED, USED, DESTROYED
        uint256 timestamp;          // When the action occurred
        bytes32 emailId;            // Associated email ID (if applicable)
    }

    // Storage
    mapping(bytes32 => EmailRecord) public emails;              // emailId => EmailRecord
    mapping(bytes32 => KeyAuditRecord[]) public keyAudits;      // keyId => audit trail
    mapping(address => bytes32[]) public userEmails;            // user => email IDs
    mapping(address => uint256) public emailCount;              // user => count of emails

    // Events
    event EmailRegistered(
        bytes32 indexed emailId,
        bytes32 indexed emailHash,
        address indexed sender,
        bytes32 qkdKeyId,
        uint256 timestamp,
        string securityLevel
    );

    event EmailVerified(
        bytes32 indexed emailId,
        address indexed verifier,
        uint256 timestamp
    );

    event KeyAuditRecorded(
        bytes32 indexed keyId,
        address indexed user,
        string action,
        uint256 timestamp
    );

    // Modifiers
    modifier emailExists(bytes32 emailId) {
        require(emails[emailId].timestamp != 0, "Email does not exist");
        _;
    }

    modifier onlyEmailSender(bytes32 emailId) {
        require(emails[emailId].sender == msg.sender, "Only sender can perform this action");
        _;
    }

    /**
     * @dev Register a new quantum-secured email on the blockchain
     * @param emailId Unique identifier for the email
     * @param emailHash SHA-256 hash of the encrypted email content
     * @param qkdKeyId Quantum key used for encryption
     * @param recipientAddress Ethereum address of recipient (optional, can be zero address)
     * @param securityLevel Security level used (OTP, QUANTUM_AIDED_AES, PQC, STANDARD)
     * @param metadata Additional metadata (IPFS hash, etc.)
     */
    function registerEmail(
        bytes32 emailId,
        bytes32 emailHash,
        bytes32 qkdKeyId,
        address recipientAddress,
        string memory securityLevel,
        string memory metadata
    ) public {
        require(emails[emailId].timestamp == 0, "Email already registered");
        require(emailHash != bytes32(0), "Invalid email hash");
        require(qkdKeyId != bytes32(0), "Invalid QKD key ID");

        emails[emailId] = EmailRecord({
            emailHash: emailHash,
            qkdKeyId: qkdKeyId,
            sender: msg.sender,
            recipient: recipientAddress,
            timestamp: block.timestamp,
            securityLevel: securityLevel,
            verified: false,
            metadata: metadata
        });

        userEmails[msg.sender].push(emailId);
        emailCount[msg.sender]++;

        emit EmailRegistered(
            emailId,
            emailHash,
            msg.sender,
            qkdKeyId,
            block.timestamp,
            securityLevel
        );
    }

    /**
     * @dev Verify an email by comparing the provided hash with stored hash
     * @param emailId Email identifier to verify
     * @param providedHash Hash to verify against stored hash
     * @return verified True if hashes match
     */
    function verifyEmail(bytes32 emailId, bytes32 providedHash)
        public
        emailExists(emailId)
        returns (bool verified)
    {
        EmailRecord storage email = emails[emailId];

        if (email.emailHash == providedHash) {
            email.verified = true;
            emit EmailVerified(emailId, msg.sender, block.timestamp);
            return true;
        }

        return false;
    }

    /**
     * @dev Get email record details
     * @param emailId Email identifier
     * @return Complete email record
     */
    function getEmail(bytes32 emailId)
        public
        view
        emailExists(emailId)
        returns (EmailRecord memory)
    {
        return emails[emailId];
    }

    /**
     * @dev Record quantum key audit event
     * @param keyId Quantum key identifier
     * @param action Action performed (OBTAINED, ACTIVATED, USED, DESTROYED)
     * @param emailId Associated email ID (can be zero if not applicable)
     */
    function recordKeyAudit(
        bytes32 keyId,
        string memory action,
        bytes32 emailId
    ) public {
        require(keyId != bytes32(0), "Invalid key ID");

        keyAudits[keyId].push(KeyAuditRecord({
            keyId: keyId,
            user: msg.sender,
            action: action,
            timestamp: block.timestamp,
            emailId: emailId
        }));

        emit KeyAuditRecorded(keyId, msg.sender, action, block.timestamp);
    }

    /**
     * @dev Get key audit trail
     * @param keyId Quantum key identifier
     * @return Array of audit records for the key
     */
    function getKeyAuditTrail(bytes32 keyId)
        public
        view
        returns (KeyAuditRecord[] memory)
    {
        return keyAudits[keyId];
    }

    /**
     * @dev Get all email IDs for a user
     * @param user User address
     * @return Array of email IDs
     */
    function getUserEmails(address user)
        public
        view
        returns (bytes32[] memory)
    {
        return userEmails[user];
    }

    /**
     * @dev Get email count for a user
     * @param user User address
     * @return Number of emails registered by user
     */
    function getUserEmailCount(address user)
        public
        view
        returns (uint256)
    {
        return emailCount[user];
    }

    /**
     * @dev Batch register multiple emails (gas efficient)
     * @param emailIds Array of email identifiers
     * @param emailHashes Array of email hashes
     * @param qkdKeyIds Array of QKD key IDs
     * @param securityLevels Array of security levels
     */
    function batchRegisterEmails(
        bytes32[] memory emailIds,
        bytes32[] memory emailHashes,
        bytes32[] memory qkdKeyIds,
        string[] memory securityLevels
    ) public {
        require(
            emailIds.length == emailHashes.length &&
            emailIds.length == qkdKeyIds.length &&
            emailIds.length == securityLevels.length,
            "Array lengths must match"
        );

        for (uint256 i = 0; i < emailIds.length; i++) {
            if (emails[emailIds[i]].timestamp == 0) {
                emails[emailIds[i]] = EmailRecord({
                    emailHash: emailHashes[i],
                    qkdKeyId: qkdKeyIds[i],
                    sender: msg.sender,
                    recipient: address(0),
                    timestamp: block.timestamp,
                    securityLevel: securityLevels[i],
                    verified: false,
                    metadata: ""
                });

                userEmails[msg.sender].push(emailIds[i]);
                emailCount[msg.sender]++;

                emit EmailRegistered(
                    emailIds[i],
                    emailHashes[i],
                    msg.sender,
                    qkdKeyIds[i],
                    block.timestamp,
                    securityLevels[i]
                );
            }
        }
    }

    /**
     * @dev Check if email is verified on blockchain
     * @param emailId Email identifier
     * @return verified True if email exists and is verified
     */
    function isEmailVerified(bytes32 emailId)
        public
        view
        returns (bool verified)
    {
        return emails[emailId].timestamp != 0 && emails[emailId].verified;
    }

    /**
     * @dev Get contract statistics
     * @return totalEmails Total number of emails registered
     * @return totalUsers Total number of unique users
     */
    function getContractStats()
        public
        view
        returns (uint256 totalEmails, uint256 totalUsers)
    {
        // Note: totalUsers would require additional tracking in production
        totalEmails = 0;
        totalUsers = 0;

        // This is a simplified version - in production, maintain counters
        return (totalEmails, totalUsers);
    }
}

