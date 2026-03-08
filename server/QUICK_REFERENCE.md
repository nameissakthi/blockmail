# 🚀 Quick Reference - Docker Hub & Deployment

## 📦 Your Docker Image
- **Image:** `sakthiveldk/quantum-mail-server:v1`
- **Size:** 1.45GB
- **Status:** ✅ Ready to push

---

## 🔥 Quick Push to Docker Hub

```bash
# Method 1: Automated (RECOMMENDED)
./push-to-dockerhub.sh

# Method 2: Manual
sudo docker login
sudo docker push sakthiveldk/quantum-mail-server:v1
sudo docker push sakthiveldk/quantum-mail-server:latest
```

**After Push:** https://hub.docker.com/r/sakthiveldk/quantum-mail-server

---

## 🌐 Best Deployment Options

### 1. 🥇 DigitalOcean ($6-12/month) - RECOMMENDED
✅ Full blockchain support + PostgreSQL + Backend
```bash
# On DigitalOcean Droplet
docker pull sakthiveldk/quantum-mail-server:v1
# Upload docker-compose.yml
docker-compose up -d
```

### 2. 🥈 Railway.app (Free tier)
⚠️ Backend only (no Ganache)
- Dashboard → New Project → Deploy from Docker Image
- Image: `sakthiveldk/quantum-mail-server:v1`
- Add PostgreSQL from templates
- Set environment variables

### 3. 🥉 Render.com (Free tier)
⚠️ Backend only (no Ganache)
- Dashboard → New Web Service
- Docker Image: `sakthiveldk/quantum-mail-server:v1`
- Create PostgreSQL database separately
- Configure environment variables

---

## ⚙️ Required Environment Variables for Cloud

```bash
DB_HOST=<cloud-postgres-host>
DB_PORT=5432
DB_NAME=blockmail
DB_USERNAME=<user>
DB_PASSWORD=<password>
JWT_SECRET=<generate-strong-secret>
BLOCKCHAIN_ENABLED=false  # or use testnet
QKD_KEYMANAGER_MOCK_MODE=true
CLIENT_URLS=https://your-frontend-domain.com
```

---

## 🔗 Pull & Run Anywhere

```bash
docker pull sakthiveldk/quantum-mail-server:v1
docker run -p 8080:8080 -e DB_HOST=... sakthiveldk/quantum-mail-server:v1
```

---

## 📚 Full Documentation
- **Complete Guide:** `DOCKER_HUB_DEPLOYMENT.md`
- **Local Deployment:** `DEPLOYMENT_SUCCESS.md`
- **API Tests:** `test_all_endpoints.sh`

---

## ⚠️ Important Notes

**Blockchain on Cloud:**
- ❌ Ganache doesn't work well on Render/Railway
- ✅ Use DigitalOcean/AWS for full blockchain
- ✅ Or disable blockchain for cloud platforms

**Database:**
- Use managed PostgreSQL on cloud platforms
- Don't run PostgreSQL in the same container

**Security:**
- Generate strong JWT_SECRET (64+ characters)
- Never commit secrets to Git
- Use environment variables

---

## 🎯 Quick Decision Guide

**Need blockchain?**
- YES → Use DigitalOcean Droplet ($6-12/month)
- NO → Use Railway/Render (Free tier)

**Budget?**
- Free → Railway.app or Render.com (limited features)
- $6-12 → DigitalOcean (full features)
- $30+ → AWS EC2 (enterprise scale)

---

**Ready to deploy? Run:** `./push-to-dockerhub.sh` 🚀

