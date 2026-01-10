# 🔐 Quantum Mail Application

A cutting-edge, quantum-secure email application that combines Quantum Key Distribution (QKD) with blockchain auditing to provide next-generation email security. Built with Electron.js frontend and Spring Boot backend.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)
![Electron](https://img.shields.io/badge/Electron-39.2.7-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Security Levels](#-security-levels)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

Quantum Mail is a revolutionary email application that implements **ETSI GS QKD 014** standard-compliant Quantum Key Distribution, offering four distinct security levels ranging from unconditionally secure One-Time Pad encryption to standard AES encryption. All cryptographic operations are audited on a blockchain for transparency and verification.

### Why Quantum Mail?

- **Quantum-Safe Security**: Resistant to both classical and quantum computer attacks
- **Multi-Level Encryption**: Choose the security level that fits your needs
- **Blockchain Auditing**: Transparent cryptographic operation verification
- **ETSI Compliant**: Follows international QKD standards
- **End-to-End Encryption**: True E2EE with quantum-secured keys

---

## 🎯 Key Features

### 🔐 Multi-Level Quantum Cryptography

#### **Level 1: Quantum Secure (OTP)**
- One-Time Pad encryption with **perfect secrecy**
- Quantum keys from ETSI GS QKD 014 compliant Key Manager
- Unconditionally secure against all attacks
- XOR-based encryption/decryption

#### **Level 2: Quantum-aided AES**
- AES-256-GCM encryption
- Quantum key seeding for enhanced security
- Authenticated encryption with associated data (AEAD)
- Web Crypto API integration

#### **Level 3: Post-Quantum Cryptography**
- Placeholder for Kyber/Dilithium algorithms
- Future-ready PQC implementation
- Currently falls back to Quantum-aided AES

#### **Level 4: Standard Encryption**
- Traditional AES-256-GCM encryption
- PBKDF2 key derivation
- No quantum keys required
- Suitable for non-critical communications

### 🔑 Quantum Key Distribution

- **ETSI GS QKD 014** compliant implementation
- Mock Key Manager for development and testing
- Automated key lifecycle: RESERVED → ACTIVE → USED → EXPIRED → DESTROYED
- Key pool maintenance with scheduled cleanup
- Real-time key status monitoring

### ⛓️ Blockchain Integration

- Cryptographic operation audit trail
- Email integrity verification
- Key usage tracking
- Transaction verification API
- Mock blockchain (ready for Hyperledger/Ethereum)

### 📧 Email Features

- User authentication with JWT tokens
- Compose emails with security level selection
- Encrypted file attachments
- Automatic email decryption
- Inbox and sent mail management
- Message integrity verification (SHA-256)
- SMTP integration for actual email delivery

### 🎨 User Interface

- Modern Electron.js desktop application
- Clean, responsive design
- Real-time KM status monitoring
- Security level badges
- Loading states and error handling
- Smooth animations and transitions

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Electron Desktop Client                   │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Auth UI   │  │   Email UI   │  │   Settings   │       │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘       │
│         │                 │                  │                │
│  ┌──────┴─────────────────┴──────────────────┴───────┐      │
│  │         Encryption Manager (Multi-Level)          │      │
│  │    OTP | Quantum-AES | PQC | Standard AES         │      │
│  └────────────────────────┬──────────────────────────┘      │
└───────────────────────────┼──────────────────────────────────┘
                            │ HTTPS/REST API
┌───────────────────────────┼──────────────────────────────────┐
│                    Spring Boot Backend                       │
│  ┌────────────────────────┴──────────────────────────┐      │
│  │            REST API Controllers                    │      │
│  │  Auth | Email | QKD | Blockchain | Verification   │      │
│  └────────────┬───────────────────┬───────────────────┘      │
│               │                   │                          │
│  ┌────────────┴──────┐  ┌─────────┴──────────┐             │
│  │   Crypto Service   │  │  Key Manager (KM)  │             │
│  │  4-Level Encrypt   │  │  ETSI GS QKD 014   │             │
│  └────────────┬───────┘  └─────────┬──────────┘             │
│               │                    │                         │
│  ┌────────────┴────────────────────┴──────────┐             │
│  │         PostgreSQL Database                │             │
│  │  Users | Emails | Keys | Audit Logs        │             │
│  └──────────────────────────────────────────────┘            │
│                                                               │
│  ┌────────────────────────────────────────────┐             │
│  │      Mock Blockchain Auditing              │             │
│  │   (Ready for Hyperledger/Ethereum)         │             │
│  └──────────────────────────────────────────────┘            │
└───────────────────────────────────────────────────────────────┘
```

---

## 💻 Technology Stack

### Frontend (Desktop Client)
- **Framework**: Electron.js 39.2.7
- **Build Tool**: Vite 5.4.21
- **HTTP Client**: Axios 1.13.2
- **State Management**: electron-store 11.0.2
- **Cryptography**: Web Crypto API
- **Package Manager**: pnpm

### Backend (Server)
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **Email**: Spring Mail (SMTP)
- **ORM**: Spring Data JPA + Hibernate
- **Build Tool**: Maven

### Security & Cryptography
- **QKD Standard**: ETSI GS QKD 014
- **Encryption**: AES-256-GCM, One-Time Pad
- **Hashing**: SHA-256
- **Key Derivation**: PBKDF2
- **Authentication**: JWT tokens

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Backend Requirements
- **Java 21** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- **PostgreSQL 14+** ([Download](https://www.postgresql.org/download/))
- **Maven 3.8+** (or use included Maven wrapper)

### Frontend Requirements
- **Node.js 18+** and **npm** ([Download](https://nodejs.org/))
- **pnpm** (install via `npm install -g pnpm`)

### System Requirements
- **OS**: Linux, macOS, or Windows
- **RAM**: 4GB minimum, 8GB recommended
- **Disk Space**: 500MB for application + database

---

## 🚀 Installation

### 1. Clone the Repository

```bash
cd ~/Desktop
git clone <repository-url> "Quantum Mail Application"
cd "Quantum Mail Application"
```

### 2. Setup PostgreSQL Database

```bash
# Start PostgreSQL service
sudo systemctl start postgresql

# Create database
sudo -u postgres psql
CREATE DATABASE blockmail;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE blockmail TO postgres;
\q
```

### 3. Backend Setup

```bash
cd server

# Build the project
./mvnw clean install

# Or on Windows
mvnw.cmd clean install
```

### 4. Frontend Setup

```bash
cd ../client-desktop

# Install dependencies
pnpm install
```

---

## ⚙️ Configuration

### Backend Configuration

Edit `server/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/blockmail
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# SMTP Configuration (for actual email sending)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD

# JWT Secret
spring.security.jwt.secret-key=YOUR_SECRET_KEY

# QKD Configuration
qkd.keymanager.mock-mode=true  # Set to false for real KM
qkd.keymanager.base-url=http://localhost:8080/api/v1/keys
```

### Frontend Configuration

Edit `client-desktop/src/api/config.js`:

```javascript
export const API_BASE_URL = 'http://localhost:8080/api';
export const KM_BASE_URL = 'http://localhost:8080/api/v1/keys';
```

---

## 🏃 Running the Application

### Start Backend Server

```bash
cd server

# Using Maven wrapper
./mvnw spring-boot:run

# Or using the convenience script
chmod +x start-backend-fixed.sh
./start-backend-fixed.sh
```

The server will start on `http://localhost:8080`

### Start Frontend Client

```bash
cd client-desktop

# Development mode
pnpm start

# Or using the convenience script
chmod +x start-frontend-fixed.sh
./start-frontend-fixed.sh

# Build for production
pnpm run make
```

### Verify Installation

1. Backend health check: `curl http://localhost:8080/api/health`
2. Open the Electron app (should launch automatically)
3. Register a new user account
4. Check KM status in the application

---

## 🔒 Security Levels

### Choosing the Right Security Level

| Level | Security | Performance | Use Case |
|-------|----------|-------------|----------|
| **Level 1: OTP** | ⭐⭐⭐⭐⭐ Unconditional | ⭐⭐⭐ Medium | Top-secret communications |
| **Level 2: Q-AES** | ⭐⭐⭐⭐ Very High | ⭐⭐⭐⭐ Fast | Sensitive business emails |
| **Level 3: PQC** | ⭐⭐⭐⭐ Future-proof | ⭐⭐⭐ Medium | Future quantum threats |
| **Level 4: Standard** | ⭐⭐⭐ High | ⭐⭐⭐⭐⭐ Very Fast | Regular communications |

### Security Considerations

- **Level 1 (OTP)**: Provides information-theoretic security but requires constant supply of quantum keys
- **Level 2 (Q-AES)**: Balances security and performance for most use cases
- **Level 3 (PQC)**: Future implementation for post-quantum cryptography
- **Level 4 (Standard)**: Uses conventional cryptography without quantum keys

---

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/verify
```

### Email Endpoints

```http
POST /api/emails/send
GET /api/emails/inbox
GET /api/emails/sent
GET /api/emails/{id}
DELETE /api/emails/{id}
PUT /api/emails/{id}/read
```

### QKD Key Manager Endpoints

```http
GET /api/v1/keys/status
POST /api/v1/keys/master_SAE/{km_id}/keys
POST /api/v1/keys/master_SAE/{km_id}/dec_keys
GET /api/v1/keys/{key_id}
```

### Blockchain Verification

```http
GET /api/blockchain/verify/{transactionId}
POST /api/blockchain/audit
```

**Full API Documentation**: Import `server/Quantum_Mail_API.postman_collection.json` into Postman.

---

## 📁 Project Structure

```
Quantum Mail Application/
├── client-desktop/               # Electron Desktop Client
│   ├── src/
│   │   ├── api/                 # API service layer
│   │   │   ├── auth.js          # Authentication
│   │   │   ├── blockchain.js    # Blockchain integration
│   │   │   ├── client.js        # HTTP client
│   │   │   ├── email.js         # Email operations
│   │   │   └── km.js            # Key Manager API
│   │   ├── components/          # UI components
│   │   │   ├── auth/            # Login & Registration
│   │   │   ├── common/          # Shared components
│   │   │   └── email/           # Email components
│   │   ├── services/            # Business logic
│   │   │   ├── encryption/      # Multi-level encryption
│   │   │   └── qkd/             # QKD services
│   │   ├── store/               # State management
│   │   └── utils/               # Utility functions
│   ├── index.html               # Main HTML
│   ├── package.json             # Dependencies
│   └── README.md                # Frontend docs
│
├── server/                       # Spring Boot Backend
│   ├── src/main/java/com/sakthivel/blockmail/
│   │   ├── config/              # Configuration classes
│   │   │   ├── MailConfig.java
│   │   │   ├── QkdKeyManagerConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/          # REST controllers
│   │   ├── dao/                 # Data Access Objects
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # Entity models
│   │   ├── repository/          # JPA repositories
│   │   ├── security/            # Security config & JWT
│   │   └── service/             # Business logic
│   │       └── crypto/          # Cryptography services
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml                  # Maven dependencies
│   └── README.md                # Backend docs
│
├── README.md                     # This file
└── docs/                         # Additional documentation
```

---

## 🧪 Testing

### Backend Tests

```bash
cd server

# Run all tests
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report

# API testing script
chmod +x test_all_endpoints.sh
./test_all_endpoints.sh
```

### Frontend Tests

```bash
cd client-desktop

# Run tests (when implemented)
pnpm test
```

### Manual Testing

1. **QKD Key Manager**: Test key request/retrieval flow
2. **Email Encryption**: Send emails with all 4 security levels
3. **Blockchain Audit**: Verify transaction logging
4. **Authentication**: Test login/register/token refresh

---

## 🔧 Troubleshooting

### Common Issues

**Problem**: Database connection fails
```bash
# Solution: Check PostgreSQL is running
sudo systemctl status postgresql
# Verify credentials in application.properties
```

**Problem**: Frontend can't connect to backend
```bash
# Solution: Ensure backend is running on port 8080
curl http://localhost:8080/api/health
# Check CORS settings in WebConfig.java
```

**Problem**: Key Manager not responding
```bash
# Solution: Verify mock mode is enabled
# Check qkd.keymanager.mock-mode=true in application.properties
```

**Problem**: Email sending fails
```bash
# Solution: Configure SMTP settings
# For Gmail, enable "App Passwords" in Google Account settings
```

---

## 🚢 Deployment

### Backend Deployment

```bash
cd server

# Create production JAR
./mvnw clean package -DskipTests

# Run production build
java -jar target/server-0.0.1-SNAPSHOT.jar

# With custom config
java -jar target/server-0.0.1-SNAPSHOT.jar --spring.config.location=/path/to/application.properties
```

### Frontend Deployment

```bash
cd client-desktop

# Build installers for all platforms
pnpm run make

# Installers will be in out/make/
# - .deb for Debian/Ubuntu
# - .rpm for Fedora/RedHat
# - .zip for portable version
```

---

## 🌐 Environment Variables

Create `.env` file for sensitive configuration:

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=blockmail
DB_USER=postgres
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000

# SMTP
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email@gmail.com
SMTP_PASSWORD=your_app_password

# QKD
QKD_MOCK_MODE=true
QKD_BASE_URL=http://localhost:8080/api/v1/keys
```

---

## 📖 Additional Documentation

- [Frontend Documentation](client-desktop/README.md) - Detailed client documentation
- [Backend Documentation](server/README.md) - Server implementation details
- [Features List](client-desktop/FEATURES.md) - Complete feature status
- [API Reference](server/QUICK_REFERENCE.md) - Quick API guide
- [Postman Collection](server/Quantum_Mail_API.postman_collection.json) - API testing

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Development Guidelines

- Follow Java coding conventions (Google Style Guide)
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

**Sakthivel**
- Email: svel7252@gmail.com
- GitHub: [@nameissakthi](https://github.com/nameissakthi)

---

## 🙏 Acknowledgments

- ETSI for the QKD 014 standard specification
- Spring Boot and Electron.js communities
- PostgreSQL development team
- All contributors and testers

---

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Email: svel7252@gmail.com
- Check existing documentation in `/docs`

---

<div align="center">

**Built with ❤️ using Quantum Technology and Spring Boot**

[Report Bug](https://github.com/nameissakthi/quantum-mail/issues) · [Request Feature](https://github.com/nameissakthi/quantum-mail/issues) · [Documentation](docs/)

</div>
