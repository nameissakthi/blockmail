# 🐳 Docker Hub & Cloud Deployment Guide

## Docker Image Information
- **Docker Hub Username:** `sakthiveldk`
- **Repository:** `quantum-mail-server`
- **Tags:** `v1`, `latest`
- **Full Image Name:** `sakthiveldk/quantum-mail-server:v1`

---

## 📤 Push to Docker Hub

### Step 1: Login to Docker Hub
```bash
sudo docker login
# Enter your Docker Hub username: sakthiveldk
# Enter your Docker Hub password/token
```

### Step 2: Push the Images
```bash
# Push version 1
sudo docker push sakthiveldk/quantum-mail-server:v1

# Push latest tag
sudo docker push sakthiveldk/quantum-mail-server:latest
```

### Step 3: Verify on Docker Hub
Visit: https://hub.docker.com/r/sakthiveldk/quantum-mail-server

---

## 🌐 Deployment Platforms

### ✅ **Recommended Platforms for This Application**

| Platform | Suitable? | Notes |
|----------|-----------|-------|
| **Render** | ⚠️ Partial | Good for backend, but Ganache blockchain won't work |
| **Railway** | ⚠️ Partial | Same as Render - database works, blockchain limited |
| **DigitalOcean** | ✅ YES | Full Docker Compose support with Droplets |
| **AWS ECS/EC2** | ✅ YES | Full control, can run all containers |
| **Azure Container** | ✅ YES | Complete Docker Compose support |
| **Google Cloud Run** | ❌ NO | Stateless only, no blockchain support |
| **Heroku** | ❌ NO | Limited Docker support, no compose |

---

## 🚀 Deployment Options

### Option 1: Render.com (Free Tier Available)

**⚠️ Limitations:**
- Can only deploy the **backend service** (not Ganache/PostgreSQL together)
- Need to use **external database** (Render PostgreSQL)
- **Blockchain features** will need to be **mocked or use testnet**

**Steps:**

1. **Create Account:** https://render.com
2. **Connect GitHub** (push your code to GitHub first)
3. **Create PostgreSQL Database:**
   - Go to Dashboard → New → PostgreSQL
   - Note down the connection details

4. **Create Web Service:**
   - Go to Dashboard → New → Web Service
   - Connect your GitHub repo
   - OR use Docker image: `sakthiveldk/quantum-mail-server:v1`
   - Set environment variables:
     ```
     DB_HOST=<render-postgres-host>
     DB_PORT=5432
     DB_NAME=blockmail
     DB_USERNAME=<from-render>
     DB_PASSWORD=<from-render>
     JWT_SECRET=<your-secret>
     BLOCKCHAIN_ENABLED=false  # Or use testnet
     QKD_KEYMANAGER_MOCK_MODE=true
     ```

5. **Deploy**

**Pricing:**
- Free tier: $0/month (with limitations)
- Paid: $7/month for web service + $7/month for database

---

### Option 2: Railway.app (Most Docker-Friendly)

**✅ Better Docker Compose Support**

**Steps:**

1. **Create Account:** https://railway.app
2. **Create New Project**
3. **Add Services:**
   - PostgreSQL (from Railway templates)
   - Your Backend (from Docker image)
   
4. **Configure Backend Service:**
   - Image: `sakthiveldk/quantum-mail-server:v1`
   - Set environment variables (same as above)
   - Add domain

**Pricing:**
- Free tier: $5 credit/month
- Pay as you go: ~$5-20/month

---

### Option 3: DigitalOcean Droplet (RECOMMENDED - Full Control)

**✅ Best for Complete Deployment with Blockchain**

**Steps:**

1. **Create Droplet:**
   - Go to: https://www.digitalocean.com/
   - Create → Droplets
   - Choose: Docker image (pre-installed Docker)
   - Plan: Basic ($6/month or $12/month)
   - Choose datacenter region

2. **SSH into Droplet:**
   ```bash
   ssh root@<your-droplet-ip>
   ```

3. **Install Docker Compose:**
   ```bash
   apt update
   apt install docker-compose -y
   ```

4. **Pull and Run:**
   ```bash
   # Pull your image
   docker pull sakthiveldk/quantum-mail-server:v1
   
   # Create docker-compose.yml on server
   nano docker-compose.yml
   # (Copy your docker-compose.yml content)
   
   # Start services
   docker-compose up -d
   ```

5. **Configure Firewall:**
   ```bash
   ufw allow 8080/tcp
   ufw allow 22/tcp
   ufw enable
   ```

6. **Add Domain (Optional):**
   - Point your domain to Droplet IP
   - Use Nginx reverse proxy with SSL

**Pricing:**
- Basic Droplet: $6-12/month
- Includes: Full Docker Compose support, Ganache, PostgreSQL

---

### Option 4: AWS EC2 with Docker (Enterprise Grade)

**Steps:**

1. **Launch EC2 Instance:**
   - AMI: Ubuntu 22.04
   - Instance Type: t2.medium or t3.medium
   - Security Group: Allow ports 22, 80, 443, 8080

2. **Install Docker:**
   ```bash
   sudo apt update
   sudo apt install docker.io docker-compose -y
   sudo systemctl start docker
   sudo systemctl enable docker
   ```

3. **Deploy Application:**
   ```bash
   docker pull sakthiveldk/quantum-mail-server:v1
   # Upload docker-compose.yml
   docker-compose up -d
   ```

4. **Setup Load Balancer (Optional):**
   - Create Application Load Balancer
   - Configure SSL/TLS

**Pricing:**
- t2.medium: ~$30-40/month
- t3.medium: ~$30-35/month
- + bandwidth costs

---

## 🔄 Quick Deploy Using Docker Image Only

**On ANY server with Docker:**

```bash
# 1. Pull the image
docker pull sakthiveldk/quantum-mail-server:v1

# 2. Run PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=sakthivel \
  -e POSTGRES_DB=blockmail \
  -p 5432:5432 \
  bitnami/postgresql:latest

# 3. Run Ganache (optional - for blockchain)
docker run -d \
  --name ganache \
  -p 7545:8545 \
  trufflesuite/ganache:v7.9.1 \
  --wallet.mnemonic "test test test test test test test test test test test junk" \
  --chain.chainId 1337

# 4. Run Backend
docker run -d \
  --name quantum-mail-backend \
  -p 8080:8080 \
  --link postgres:postgres \
  --link ganache:ganache \
  -e DB_HOST=postgres \
  -e DB_PORT=5432 \
  -e DB_NAME=blockmail \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=sakthivel \
  -e JWT_SECRET=your-secret-key \
  -e BLOCKCHAIN_RPC_URL=http://ganache:8545 \
  sakthiveldk/quantum-mail-server:v1
```

---

## 📝 Deployment Comparison

| Feature | Render | Railway | DigitalOcean | AWS EC2 |
|---------|--------|---------|--------------|---------|
| **Cost (Start)** | Free/$7 | Free/$5 | $6 | $30 |
| **Blockchain Support** | ❌ No | ❌ No | ✅ Yes | ✅ Yes |
| **Full Docker Compose** | ❌ No | ⚠️ Limited | ✅ Yes | ✅ Yes |
| **PostgreSQL** | ✅ Yes | ✅ Yes | ✅ Yes | ✅ Yes |
| **Easy Setup** | ✅ Very Easy | ✅ Easy | ⚠️ Medium | ⚠️ Complex |
| **Auto-scaling** | ✅ Yes | ✅ Yes | ⚠️ Manual | ✅ Yes |
| **Free Tier** | ✅ Yes | ✅ Yes | ❌ No | ✅ Yes* |

*AWS Free Tier: t2.micro for 12 months

---

## 🎯 My Recommendation

### For Development/Testing:
**Use Railway or Render** - Easy setup, free tier available

### For Production with Full Blockchain:
**Use DigitalOcean Droplet ($6-12/month)** - Best value, full control, all features work

### For Enterprise/Scale:
**Use AWS EC2 with Load Balancer** - Most scalable, but more complex

---

## 🔐 Important: Environment Variables for Production

Create a `.env.production` file:

```bash
# Database (Use managed database on cloud)
DB_HOST=<cloud-db-host>
DB_PORT=5432
DB_NAME=blockmail
DB_USERNAME=<db-user>
DB_PASSWORD=<strong-password>

# JWT (Generate strong secret)
JWT_SECRET=<generate-strong-random-string-64-chars>

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<app-password>

# Blockchain (Use testnet or disable for cloud)
BLOCKCHAIN_ENABLED=false  # or use Ethereum testnet
BLOCKCHAIN_RPC_URL=https://sepolia.infura.io/v3/<your-key>

# QKD
QKD_KEYMANAGER_MOCK_MODE=true

# CORS (Add your frontend domain)
CLIENT_URLS=https://your-frontend-domain.com
```

---

## 📚 Additional Resources

- **Docker Hub:** https://hub.docker.com/r/sakthiveldk/quantum-mail-server
- **Render Docs:** https://render.com/docs
- **Railway Docs:** https://docs.railway.app
- **DigitalOcean Docs:** https://docs.digitalocean.com/products/droplets/
- **AWS ECS Docs:** https://docs.aws.amazon.com/ecs/

---

## ⚠️ Important Notes

1. **Blockchain on Cloud:**
   - Ganache is for **development only**
   - For production, use **Ethereum testnet** (Sepolia/Goerli) or **disable blockchain**
   - Cloud platforms don't support running Ganache efficiently

2. **Database:**
   - Use **managed databases** on cloud (more reliable)
   - Don't run PostgreSQL in same container in production

3. **Security:**
   - Always use **strong JWT secrets**
   - Enable **SSL/TLS** (use Nginx reverse proxy)
   - Use **environment variables**, never hardcode secrets

4. **Cost Optimization:**
   - Start with **smaller instances** and scale up
   - Monitor resource usage
   - Use **managed services** for database

---

**Ready to deploy? Follow the Docker Hub push commands above!** 🚀

