# 🚀 Quantum Mail - Quick Reference Card

## 📦 Start Application
```bash
cd "/home/sakthivel/Desktop/Quantum Mail Application/server"
./mvnw spring-boot:run
```

## 🔐 Test the System (Copy-Paste Ready)

### 1️⃣ Register User
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@test.com","password":"pass123"}'
```

### 2️⃣ Login & Save Token
```bash
export TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"pass123"}' | tr -d '"')

echo "JWT Token: $TOKEN"
```

### 3️⃣ Get Quantum Keys
```bash
curl -X POST http://localhost:8080/api/qkd/obtain-keys \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"numberOfKeys":5,"keySize":256}'
```

### 4️⃣ Check Key Status
```bash
curl http://localhost:8080/api/qkd/key-status \
  -H "Authorization: Bearer $TOKEN"
```

### 5️⃣ Send Quantum Email
```bash
curl -X POST http://localhost:8080/api/quantum-email/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail":"bob@test.com",
    "subject":"Top Secret",
    "content":"This is quantum-secured!",
    "securityLevel":"QUANTUM_AIDED_AES"
  }'
```

### 6️⃣ View Sent Emails
```bash
curl http://localhost:8080/api/quantum-email/sent \
  -H "Authorization: Bearer $TOKEN"
```

## 🔑 Security Levels

| Level | Name | Algorithm | Use Case |
|-------|------|-----------|----------|
| 1 | `QUANTUM_SECURE_OTP` | One-Time Pad | Maximum security |
| 2 | `QUANTUM_AIDED_AES` | AES + Quantum seed | **Recommended** |
| 3 | `POST_QUANTUM_CRYPTO` | PQC (Kyber) | Future-proof |
| 4 | `STANDARD_ENCRYPTION` | AES-256-GCM | Compatibility |

## 📡 All Endpoints

### Authentication
- `POST /register` - Register user
- `POST /login` - Get JWT token

### QKD Management
- `POST /api/qkd/obtain-keys` - Get quantum keys
- `GET /api/qkd/key-status` - Check keys
- `POST /api/qkd/activate-key/{keyId}` - Activate
- `DELETE /api/qkd/destroy-key/{keyId}` - Destroy

### Quantum Email
- `POST /api/quantum-email/send` - Send
- `GET /api/quantum-email/sent` - Sent list
- `GET /api/quantum-email/received` - Inbox
- `GET /api/quantum-email/decrypt/{emailId}` - Decrypt

### Blockchain
- `GET /api/blockchain/verify/{txHash}` - Verify
- `GET /api/blockchain/transaction/{txHash}` - Details

## ⚙️ Configuration

### application.properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/blockmail
spring.datasource.username=postgres
spring.datasource.password=sakthivel

# QKD Key Manager
qkd.keymanager.mock-mode=true
qkd.keymanager.key-pool-size=10
qkd.keymanager.key-lifetime-seconds=3600

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## 🔄 Background Jobs

- **Every 5 min**: Cleanup expired keys
- **Every 10 min**: Maintain key pool

## 📊 Build Commands

```bash
# Clean build
./mvnw clean compile

# Run tests
./mvnw test

# Package JAR
./mvnw clean package

# Run application
./mvnw spring-boot:run

# Run packaged JAR
java -jar target/server-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| App won't start | Check PostgreSQL running |
| No quantum keys | Call `/api/qkd/obtain-keys` |
| Auth fails | Include `Authorization: Bearer <token>` |
| DB error | Create database: `blockmail` |

## 📚 Documentation

- **README.md** - Complete guide
- **IMPLEMENTATION_GUIDE.md** - Technical details
- **SUMMARY.md** - Implementation summary
- **This file** - Quick reference

## ✅ Status

- ✅ **Build**: SUCCESS
- ✅ **Tests**: Passing
- ✅ **Docs**: Complete
- ✅ **API**: Functional
- ✅ **Ready**: Production architecture

## 🎯 Quick Test Script

```bash
#!/bin/bash
# Test the entire flow

# 1. Register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@mail.com","password":"test123"}'

# 2. Login
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}' | tr -d '"')

echo "Token: $TOKEN"

# 3. Get keys
curl -X POST http://localhost:8080/api/qkd/obtain-keys \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"numberOfKeys":3,"keySize":256}'

# 4. Send email
curl -X POST http://localhost:8080/api/quantum-email/send \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail":"receiver@test.com",
    "subject":"Test",
    "content":"Hello Quantum World!",
    "securityLevel":"QUANTUM_AIDED_AES"
  }'

# 5. Check sent
curl http://localhost:8080/api/quantum-email/sent \
  -H "Authorization: Bearer $TOKEN"
```

---

**🎉 You're all set! Run the application and start testing!**

```bash
./mvnw spring-boot:run
```

**Access**: http://localhost:8080  
**Status**: ✅ READY

