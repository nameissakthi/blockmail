# BlockMail - Quantum Secure Email Client

A desktop email client application built with Electron.js that integrates Quantum Key Distribution (QKD) for enhanced email security.

## Features

### Multi-Level Security Configuration

1. **Level 1 - Quantum Secure (OTP)**: Uses One Time Pad with quantum keys for unconditional security
2. **Level 2 - Quantum-aided AES**: AES-256 encryption with quantum key seeding
3. **Level 3 - Post-Quantum Cryptography**: PQC algorithms (placeholder for future implementation)
4. **Level 4 - Standard**: Standard AES encryption without quantum security

### Core Functionality

- User authentication (register/login)
- Key Manager (KM) integration using ETSI GS QKD 014 protocol
- Email composition with multi-level encryption
- Inbox and sent email management
- Email viewing with automatic decryption
- Attachment support
- Real-time KM status monitoring

## Project Structure

```
client-desktop/
├── src/
│   ├── api/                    # API service layer
│   │   ├── auth.js            # Authentication API
│   │   ├── client.js          # HTTP client with interceptors
│   │   ├── config.js          # API configuration
│   │   ├── email.js           # Email operations API
│   │   └── km.js              # Key Manager API (ETSI GS QKD 014)
│   │
│   ├── components/             # UI components
│   │   ├── auth/
│   │   │   ├── Login.js       # Login component
│   │   │   └── Register.js    # Registration component
│   │   └── email/
│   │       ├── Compose.js     # Email composition
│   │       ├── EmailList.js   # Inbox/Sent list view
│   │       └── EmailView.js   # Single email view
│   │
│   ├── services/               # Business logic services
│   │   └── encryption/
│   │       ├── encryptionManager.js  # Main encryption orchestrator
│   │       ├── otp.js               # One Time Pad implementation
│   │       ├── quantumAes.js        # Quantum-aided AES
│   │       ├── securityLevels.js    # Security level definitions
│   │       └── standard.js          # Standard AES encryption
│   │
│   ├── store/                  # State management
│   │   └── store.js           # Application state store
│   │
│   ├── index.css              # Application styles
│   ├── main.js                # Electron main process
│   ├── preload.js             # Electron preload script
│   └── renderer.js            # Application entry point
│
├── index.html                 # Main HTML file
├── package.json               # Dependencies and scripts
└── forge.config.js            # Electron Forge configuration
```

## Installation

1. Install dependencies:
```bash
pnpm install
```

## Configuration

### Backend API Configuration

Update the backend API URL in `src/api/config.js`:

```javascript
export const API_CONFIG = {
  BASE_URL: 'http://localhost:3000/api',  // Update this to your backend URL
  // ...
};
```

### Backend Requirements

Your backend should implement the following endpoints:

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/verify` - Verify token
- `POST /api/auth/refresh` - Refresh token

#### Key Manager (ETSI GS QKD 014)
- `GET /api/km/status` - Get KM status
- `POST /api/km/key` - Get a single quantum key
- `POST /api/km/keys` - Get multiple quantum keys
- `GET /api/km/key/status/:keyId` - Get key status

#### Email Operations
- `GET /api/email/inbox` - Get inbox emails
- `GET /api/email/sent` - Get sent emails
- `GET /api/email/:id` - Get specific email
- `POST /api/email/send` - Send email
- `DELETE /api/email/:id` - Delete email
- `PUT /api/email/read/:id` - Mark email as read

## Running the Application

### Development Mode
```bash
pnpm start
```

This will start the Electron application in development mode with hot reload.

### Build for Production

Package the application:
```bash
pnpm run package
```

Create installers:
```bash
pnpm run make
```

## Usage Guide

### 1. Authentication

When you first launch the application:
1. Register a new account or login with existing credentials
2. The application will authenticate with both your email service and KM service

### 2. Composing Emails

1. Click the "✉️ Compose" button in the sidebar
2. Enter recipient email address
3. Add subject and message
4. Select security level:
   - **Quantum Secure**: Requires quantum keys from KM
   - **Quantum-aided AES**: Uses quantum keys for AES seeding
   - **Standard**: Regular encryption
5. Optionally attach files
6. Click "Send Email"

### 3. Reading Emails

1. Click on any email in your inbox
2. The email will be automatically decrypted based on its security level
3. For quantum-secured emails, the app will fetch the required key from KM
4. You can reply or delete emails from the view

### 4. Security Level Indicators

Each email shows a colored badge indicating its security level:
- 🔐 **Green**: Quantum Secure (OTP)
- 🛡️ **Blue**: Quantum-aided AES
- 🔒 **Orange**: Post-Quantum Cryptography
- 🔓 **Gray**: Standard encryption

### 5. Key Manager Status

The sidebar footer shows the KM connection status:
- 🟢 **Green**: KM is connected and operational
- 🔴 **Red**: KM is offline or error occurred

## Security Features

### Quantum Key Distribution (QKD)

The application integrates with a Key Manager following ETSI GS QKD 014 protocol:
- Quantum keys are fetched on-demand for each encrypted email
- Keys are used once (One Time Pad) for Level 1 security
- Keys seed AES-256 encryption for Level 2 security

### Encryption Flow

**Sending Email:**
1. User composes email and selects security level
2. App fetches quantum key from KM (if required)
3. Message is encrypted using selected algorithm
4. Encrypted message is sent to recipient via backend
5. Encryption metadata (keyId, algorithm) is attached

**Reading Email:**
1. App fetches encrypted email from backend
2. App retrieves decryption key from KM using keyId
3. Message is decrypted using appropriate algorithm
4. Decrypted message is displayed to user

## Development Notes

### Adding New Features

To add a new component:
1. Create component file in `src/components/`
2. Import and use in `src/renderer.js`
3. Add corresponding styles to `src/index.css`

To add a new API endpoint:
1. Update `src/api/config.js` with endpoint path
2. Add method in appropriate API service file

### Implementing PQC (Level 3)

Post-Quantum Cryptography (Level 3) is currently a placeholder. To implement:

1. Install PQC library (e.g., libsodium for Kyber):
```bash
pnpm install libsodium-wrappers
```

2. Create `src/services/encryption/pqc.js`:
```javascript
import sodium from 'libsodium-wrappers';

export class PQCEncryption {
  async encrypt(message, publicKey) {
    await sodium.ready;
    // Implement Kyber/Dilithium encryption
  }
  
  async decrypt(ciphertext, privateKey) {
    await sodium.ready;
    // Implement Kyber/Dilithium decryption
  }
}
```

3. Update `encryptionManager.js` to use PQC for level 3

## Troubleshooting

### Backend Connection Issues

If you see "No response from server" errors:
1. Verify your backend is running
2. Check the `BASE_URL` in `src/api/config.js`
3. Ensure CORS is properly configured on your backend

### KM Connection Issues

If KM shows as offline:
1. Verify your Key Manager service is running
2. Check backend logs for KM connection errors
3. Ensure ETSI GS QKD 014 protocol is properly implemented

### Decryption Failures

If emails fail to decrypt:
1. Verify the quantum key is still available in KM
2. Check that the keyId matches between encryption and decryption
3. Ensure the correct security level is being used

## Production Deployment

### Security Considerations

1. **API Keys**: Store API keys securely using electron-store
2. **Token Storage**: Use secure storage for authentication tokens
3. **HTTPS**: Always use HTTPS for production API calls
4. **Key Storage**: Never store quantum keys locally - fetch from KM on demand

### Build Configuration

Update `forge.config.js` for your platform:
- Windows: Configure Squirrel installer
- macOS: Configure DMG/PKG
- Linux: Configure DEB/RPM packages

## Contributing

This is a demonstration application for quantum-secure email communication. Contributions are welcome!

## License

MIT License

## Contact

For questions or support, please refer to the project repository.

---

**Note**: This application requires a compatible backend server implementing the BlockMail API and a Key Manager service implementing ETSI GS QKD 014 protocol.

