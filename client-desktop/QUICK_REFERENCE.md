# 🎯 Quick Reference Card - BlockMail Frontend

## ⚡ Quick Start (3 Commands)

```bash
# 1. Start your backend (port 8080)
cd ../server && ./mvnw spring-boot:run

# 2. Start frontend (in new terminal)
cd client-desktop && pnpm start

# 3. Open browser and register/login
```

---

## 🔧 Configuration

### .env File
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_ENV=development
VITE_ENABLE_DEVTOOLS=false
```

**Location:** `/client-desktop/.env`  
**Security:** ✅ Gitignored, safe to modify

---

## 📍 API Endpoints (Your Backend)

### No /api Prefix
- `POST /register` - Register user
- `POST /login` - Login user
- `GET /user/list` - List users

### With /api Prefix
- `POST /api/qkd/obtain-keys` - Get quantum keys
- `GET /api/qkd/key-status` - QKD status
- `POST /api/quantum-email/send` - Send email
- `GET /api/quantum-email/received` - Inbox
- `GET /api/quantum-email/sent` - Sent emails
- `GET /api/quantum-email/decrypt/{id}` - Read email

---

## 🐛 Quick Troubleshooting

### White Screen?
```bash
pkill -f electron
rm -rf node_modules .vite
pnpm install && pnpm start
```

### CORS Error?
Add to backend:
```java
@CrossOrigin(origins = "*")
```

### Connection Refused?
```bash
# Check backend is running
curl http://localhost:8080/register

# Expected: Response (not "Connection refused")
```

### 401 Unauthorized?
```javascript
// In DevTools Console (F12)
localStorage.clear();
location.reload();
// Then login again
```

---

## 🔍 DevTools Debugging

### Open DevTools
**Windows/Linux:** `Ctrl + Shift + I`  
**Mac:** `Cmd + Option + I`

### Check API Calls
1. Go to **Network** tab
2. Try to login
3. Look for request to `http://localhost:8080/login`
4. Check Status (should be 200)
5. Check Response (should have token)

### Check Console Errors
1. Go to **Console** tab
2. Look for red error messages
3. Check if they're about network/API
4. Fix accordingly

---

## ✅ Verification Checklist

### Files Exist
- [ ] `.env` file exists
- [ ] Shows `VITE_API_BASE_URL=http://localhost:8080`

### Backend Running
- [ ] Backend started on port 8080
- [ ] Can curl: `curl http://localhost:8080/register`

### Frontend Running
- [ ] `pnpm start` executed successfully
- [ ] Electron window opens
- [ ] Login/Register screen visible
- [ ] No white screen

### Can Register
- [ ] Click "Register here"
- [ ] Fill form and submit
- [ ] Network tab shows `POST /register`
- [ ] Status 200/201
- [ ] Redirects to main app

### Can Login
- [ ] Enter credentials
- [ ] Click "Sign In"
- [ ] Network tab shows `POST /login`
- [ ] Status 200
- [ ] Token stored in localStorage

### QKD Status
- [ ] Sidebar shows QKD status
- [ ] Either 🟢 Connected or 🔴 Offline
- [ ] Network tab shows `/api/qkd/key-status`

---

## 📚 Documentation Files

1. **START_HERE.md** - Complete overview
2. **QUICKSTART.md** - 5-minute guide
3. **API_FIX_CHANGELOG.md** - All fixes applied
4. **FIXES_SUMMARY.md** - Summary of changes
5. **BACKEND_INTEGRATION.md** - API specifications
6. **CONNECTION_CHECKLIST.md** - Testing guide

---

## 🔑 Key Changes Made

1. ✅ Fixed corrupted files
2. ✅ Updated API endpoints to match backend
3. ✅ Added environment variable support (.env)
4. ✅ Renamed KM → QKD throughout
5. ✅ Created blockchain API
6. ✅ Fixed white screen issue

---

## 🎯 Testing Flow

```
Start Backend → Start Frontend → Open App
                       ↓
              Login Screen Appears
                       ↓
              Register New Account
                       ↓
              Login Successfully
                       ↓
              See Main App (Inbox)
                       ↓
              Check QKD Status
                       ↓
              Compose & Send Email
                       ↓
              ✅ SUCCESS!
```

---

## 📞 Need Help?

### Check These First:
1. Backend is running on port 8080
2. `.env` file has correct URL
3. DevTools Console for errors
4. DevTools Network tab for failed requests

### Common Issues:
- **White Screen** → Clear cache and rebuild
- **CORS Error** → Fix backend CORS config
- **401 Error** → Clear localStorage and login again
- **Connection Error** → Check backend is running

---

## 🚀 All Systems Ready!

Everything is fixed and configured. Just:
1. Start backend
2. Run `pnpm start`
3. Start testing!

**Your quantum-secure email client is ready to go! 🔐📧**

---

**Quick Ref Card - Save this for easy access!**

