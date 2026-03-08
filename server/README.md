# 🔐 Quantum Mail Backend
> Blockchain-powered quantum-secured email backend with multi-level encryption

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Solidity](https://img.shields.io/badge/Solidity-0.8.19-blue.svg)](https://soliditylang.org/)

---

## ✨ Features

- 🔐 **Quantum Key Distribution (QKD)** - ETSI GS QKD 014 compliant simulation
- ⛓️ **Real Blockchain Integration** - Ethereum smart contracts deployed on Ganache
- 📧 **Secure Email** - Multi-level encryption (OTP, Quantum-AES, PQC, Standard)
- 🔒 **JWT Authentication** - Email-based secure authentication
- 📊 **Immutable Audit Trail** - All email operations recorded on blockchain
- 🎯 **Multi-Level Security** - Choose your encryption level per email

---

## 🚀 Quick Start

### Prerequisites
- **Java 21+** - `java -version`
- **PostgreSQL** - Running in Docker: `sudo docker start postgres`
- **Node.js & NPM** - For blockchain: `node --version`
- **Ganache** - Local blockchain (download from [trufflesuite.com](https://trufflesuite.com/ganache/))
- **Maven 3.6+** - Included via wrapper (`./mvnw`)

### Option 1: Quick Start (Automated)
```bash
# 1. Start PostgreSQL
sudo docker start postgres

# 2. Start Ganache on port 7545

# 3. Setup blockchain (first time only)
./setup-blockchain.sh

# 4. Start application
./start.sh
```

### Option 2: Manual Start
```bash
# 1. Start services
sudo docker start postgres
# Start Ganache GUI on port 7545

# 2. Configure environment
cp .env.example .env
# Edit .env with your settings (see Configuration section)

# 3. Deploy smart contract (first time only)
npm install
npx hardhat compile
npx hardhat run scripts/deploy.js --network ganache

# 4. Run application
./mvnw spring-boot:run
```

### Option 3: Using IDE
1. Import project into IntelliJ IDEA / Eclipse / VS Code
2. Ensure PostgreSQL and Ganache are running
3. Configure `.env` file (copy from `.env.example`)
4. Run `ServerApplication.java`

### Option 4: Docker (Production Ready)
```bash
# Step 1: Configure environment
cp .env.example .env
# Edit .env: Set DB_PASSWORD, MAIL_USERNAME, MAIL_PASSWORD, JWT_SECRET, GANACHE_PRIVATE_KEY

# Step 2: Build and run with Docker Compose (includes PostgreSQL + Ganache + App)
sudo docker compose up --build -d

# Step 3: View logs
sudo docker compose logs -f quantum-mail-backend

# Stop services
sudo docker compose down
```

**OR Build Docker image only:**
```bash
sudo docker build -t quantum-mail-server:v2 .
```

**Application URL:** http://localhost:8080

---

## ⚙️ Configuration

### Environment Variables (.env)
```bash
# Copy template
cp .env.example .env

# Edit and set these required values:
DB_PASSWORD=your_postgres_password
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
GANACHE_PRIVATE_KEY=0xYOUR_PRIVATE_KEY_FROM_GANACHE
JWT_SECRET=generate_with_openssl_rand_hex_64
BLOCKCHAIN_CONTRACT_ADDRESS=deployed_contract_address
```

**Get Ganache Private Key:**
1. Open Ganache GUI
2. Click the 🔑 key icon next to the first account
3. Copy the private key
4. Paste in `.env` as `GANACHE_PRIVATE_KEY=0x...`

**Get Gmail App Password:**
1. Visit https://myaccount.google.com/apppasswords
2. Create new app password
3. Copy and paste in `.env`

---
---

## 📚 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/register` | Register new user with email/password | ❌ |
| POST | `/login` | Login and get JWT token | ❌ |
| GET | `/user/list` | Get all users | ✅ |

**Example - Register:**
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

**Example - Login:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### 📧 Quantum Email
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/quantum-email/send` | Send quantum-encrypted email | ✅ |
| GET | `/api/quantum-email/sent` | Get sent emails | ✅ |
| GET | `/api/quantum-email/received` | Get received emails | ✅ |
| GET | `/api/quantum-email/decrypt/{id}` | Decrypt specific email | ✅ |

**Example - Send Email:**
```bash
curl -X POST http://localhost:8080/api/quantum-email/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "recipientEmail": "recipient@example.com",
    "subject": "Test Email",
    "content": "Hello World",
    "securityLevel": "QUANTUM_AIDED_AES"
  }'
```

### 🔑 Quantum Key Management (QKD)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/qkd/obtain-keys` | Obtain quantum keys from KM | ✅ |
| GET | `/api/qkd/key-status` | Get active key count | ✅ |
| POST | `/api/qkd/activate-key/{keyId}` | Activate reserved key | ✅ |
| DELETE | `/api/qkd/destroy-key/{keyId}` | Securely destroy key | ✅ |

### ⛓️ Blockchain Verification
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/blockchain/verify/{txHash}` | Verify blockchain transaction | ✅ |
| GET | `/api/blockchain/transaction/{txHash}` | Get transaction details | ✅ |

---

## 🔐 Security Levels

| Level | Name | Description | Use Case |
|-------|------|-------------|----------|
| 1 | **ONE_TIME_PAD** | Perfect secrecy with quantum keys | Maximum security |
| 2 | **QUANTUM_AIDED_AES** | AES-256 with quantum seed | ⭐ Recommended |
| 3 | **POST_QUANTUM_CRYPTO** | Quantum-resistant algorithms | Future-proof |
| 4 | **STANDARD** | Traditional AES-256-GCM | Legacy compatibility |

---

## 🧪 Testing

### Automated Test Suite
```bash
# Run all endpoint tests
./test_all_endpoints.sh

# Expected: 12/13 tests pass
# (Registration may fail if user already exists)
```

### Manual Testing
```bash
# Health check
curl http://localhost:8080/actuator/health

# Register user
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'
```

---
---

## ⛓️ Blockchain Setup

### First Time Setup
```bash
# 1. Install dependencies
npm install

# 2. Start Ganache on port 7545

# 3. Compile contracts
npx hardhat compile

# 4. Deploy to Ganache
npx hardhat run scripts/deploy.js --network ganache

# 5. Copy contract address to .env
# Update BLOCKCHAIN_CONTRACT_ADDRESS in .env file

# 6. Enable blockchain
# Set BLOCKCHAIN_ENABLED=true in .env
```

### Using Automated Script
```bash
./setup-blockchain.sh
# Follow prompts and add Ganache private key when asked
```

### Redeploy Contract (if needed)
```bash
# If you reset Ganache or need fresh deployment
npx hardhat run scripts/deploy.js --network ganache

# Update .env with new contract address
# Restart application
```

### View Blockchain Transactions
- Open Ganache GUI → "Transactions" tab
- See all email registrations and verifications on blockchain

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java 21, Spring Boot 4.0.1 |
| **Database** | PostgreSQL 16 |
| **Blockchain** | Solidity 0.8.19, Hardhat, Ganache |
| **Smart Contracts** | Web3j 4.10.3 |
| **Security** | JWT, BCrypt, AES-256-GCM |
| **Email** | JavaMail, Gmail SMTP |
| **Build** | Maven 3.9+ |

---

## 📁 Project Structure

```
server/
├── contracts/                      # Solidity smart contracts
│   └── QuantumMailRegistry.sol    # Main registry contract
├── scripts/                        # Blockchain deployment scripts
│   └── deploy.js                  # Contract deployment
├── src/main/java/com/sakthivel/blockmail/
│   ├── controller/                # REST API controllers
│   ├── service/                   # Business logic
│   ├── model/                     # JPA entities
│   ├── repository/                # Data access layer
│   ├── config/                    # Spring configuration
│   ├── security/                  # JWT & authentication
│   └── dto/                       # Data transfer objects
├── .env                           # Environment variables (DO NOT commit)
├── .env.example                   # Template for .env
├── hardhat.config.js              # Blockchain configuration
├── setup-blockchain.sh            # Blockchain setup script
├── start.sh                       # Application startup script
├── test_all_endpoints.sh          # API test suite
└── README.md                      # This file
```

---

## 🔍 Troubleshooting

### Application Won't Start

**Issue: Port 8080 already in use**
```bash
sudo lsof -i :8080
sudo kill -9 <PID>
```

**Issue: PostgreSQL connection refused**
```bash
sudo docker start postgres
sudo docker ps | grep postgres
```

**Issue: Ganache not connected**
```bash
# Ensure Ganache is running on port 7545
curl -X POST --data '{"jsonrpc":"2.0","method":"net_version","params":[],"id":1}' \
  http://127.0.0.1:7545
```

### Blockchain Errors

**Issue: Invalid contract address / Contract not deployed**
```bash
# Redeploy contract
npx hardhat run scripts/deploy.js --network ganache
# Update BLOCKCHAIN_CONTRACT_ADDRESS in .env
# Restart application
```

**Issue: Nonce too low / Transaction underpriced**
```bash
# Reset Ganache (click "Restart" in Ganache GUI)
# Redeploy contract
npx hardhat run scripts/deploy.js --network ganache
```

**Issue: Hardhat compilation errors**
```bash
# Clean and rebuild
rm -rf cache artifacts node_modules
npm install
npx hardhat compile
```

### Build Errors

**Issue: Maven build fails**
```bash
# Clean rebuild
./mvnw clean install -DskipTests

# Clear Maven cache if needed
rm -rf ~/.m2/repository/com/sakthivel
./mvnw clean install
```

**Issue: Dependencies not resolving**
```bash
# Update dependencies
./mvnw dependency:resolve
./mvnw clean compile
```

---

## 🚢 Deployment

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Production Checklist
- [ ] Use real blockchain network (Testnet/Mainnet)
- [ ] Update JWT secret (strong 256-bit key)
- [ ] Configure production database
- [ ] Set up SSL/TLS certificates
- [ ] Enable production logging
- [ ] Configure proper CORS origins
- [ ] Set up monitoring and alerts
- [ ] Backup database regularly

---

## 🌐 Frontend Integration

### Recommended Frameworks
- **Electron.js** ⭐ Best for desktop (like MS Outlook)
- **Flutter Desktop** - Cross-platform alternative
- **React + Tauri** - Lightweight desktop app
- **Qt/C++** - Native performance

### CORS Configuration
Backend is pre-configured for:
- `http://localhost:5174` (Vite)
- Can add more origins in `WebConfig.java`

### Integration Example
```javascript
// Login
const response = await fetch('http://localhost:8080/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
const { token } = await response.json();

// Use token for authenticated requests
const emails = await fetch('http://localhost:8080/api/quantum-email/sent', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

---

## 📊 Architecture

```
┌─────────────┐
│  Frontend   │ (Electron/React/Flutter)
└──────┬──────┘
       │ HTTP/REST API
┌──────▼──────────────────────┐
│  Spring Boot Backend        │
│  - JWT Auth                 │
│  - Email Service            │
│  - QKD Simulation          │
│  - Encryption Engine        │
└──┬──────────────────────┬───┘
   │                      │
   │ JDBC                 │ Web3j
   ▼                      ▼
┌──────────┐      ┌────────────────┐
│PostgreSQL│      │ Ganache/Ethereum│
│ Database │      │ Smart Contracts │
└──────────┘      └────────────────┘
```

---

## 🔐 Security Features

- ✅ **JWT Authentication** - Secure token-based auth
- ✅ **BCrypt Password Hashing** - Industry-standard hashing
- ✅ **AES-256-GCM Encryption** - Military-grade encryption
- ✅ **Quantum Key Distribution** - Quantum-secure key exchange
- ✅ **Blockchain Verification** - Immutable audit trail
- ✅ **CORS Protection** - Configured origins only
- ✅ **SQL Injection Prevention** - JPA prepared statements
- ✅ **XSS Protection** - Input sanitization

---

## 📝 Essential Scripts

### start.sh - Start Application
```bash
./start.sh
# Starts Spring Boot application in background
```

### setup-blockchain.sh - Blockchain Setup
```bash
./setup-blockchain.sh
# One-time setup for blockchain integration
```

### test_all_endpoints.sh - API Testing
```bash
./test_all_endpoints.sh
# Tests all 13 API endpoints
```

---

## 📋 How to Run - Complete Steps

### Daily Development Workflow
```bash
# 1. Start PostgreSQL
sudo docker start postgres

# 2. Start Ganache GUI (if using blockchain)

# 3. Run application
./mvnw spring-boot:run
# OR
./start.sh

# 4. Test changes
./test_all_endpoints.sh

# 5. Make code changes and restart as needed
```

### Commands Reference
```bash
# Clean build
./mvnw clean compile

# Run tests
./mvnw test

# Package JAR
./mvnw clean package

# Run packaged JAR
java -jar target/server-0.0.1-SNAPSHOT.jar

# Deploy contract
npx hardhat run scripts/deploy.js --network ganache

# View logs (if using start.sh)
tail -f logs/application.log
```

---

## 🎯 Status

✅ **Production Ready**
- All 13 API endpoints functional
- Real blockchain integration (Ganache)
- Smart contract deployed and verified
- Email system working (Gmail SMTP)
- JWT authentication implemented
- Multi-level encryption operational
- Docker deployment ready
- Comprehensive test suite included

**Deployed Contract:**
- Address: Check `deployment-info.json`
- Network: Ganache (localhost:7545)
- Blockchain: Ethereum-compatible

---

## 📞 Support

### Files to Check When Troubleshooting
1. `.env` - Ensure all variables are set correctly
2. `logs/application.log` - Application logs
3. `deployment-info.json` - Contract deployment details
4. Terminal output when running

### Common Configuration Issues
- Missing `.env` file → Copy from `.env.example`
- Wrong Ganache port → Must be 7545
- PostgreSQL not running → `sudo docker start postgres`
- Contract not deployed → Run `setup-blockchain.sh`

---

## 👤 Author

**Sakthivel**
- Email: svel7252@gmail.com
- GitHub: [Your GitHub Profile]

---

## 📄 License

This project is developed for educational and research purposes.

---

**Built with ❤️ using Java, Spring Boot, Solidity, Hardhat, and Web3j**

**Last Updated:** January 14, 2026  
**Version:** 0.0.1-SNAPSHOT

