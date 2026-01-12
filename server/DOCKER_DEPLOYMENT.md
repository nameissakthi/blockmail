# 🐳 Docker Deployment Guide - Quantum Mail Backend

This guide provides instructions for deploying the Quantum Mail Backend using Docker.

## 📋 Prerequisites

- Docker Engine 20.10+ installed
- Docker Compose 2.0+ installed
- At least 2GB of free RAM
- Ports 8080 and 5432 available

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

Deploy the entire stack (Backend + PostgreSQL) with a single command:

```bash
# Build and start all services
sudo docker compose up -d

# View logs
sudo docker compose logs -f

# Stop all services
sudo docker compose down

# Stop and remove volumes (clean slate)
sudo docker compose down -v
```

### Option 2: Using Dockerfile Only

Build and run the backend container manually:

```bash
# Build the Docker image
sudo docker build -t quantum-mail-backend:latest .

# Run the container (requires external PostgreSQL)
sudo docker run -d \
  --name quantum-mail-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/blockmail \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=sakthivel \
  -e SERVER_ADDRESS=0.0.0.0 \
  quantum-mail-backend:latest
```

## 📦 Docker Architecture

### Multi-Stage Build

The Dockerfile uses a multi-stage build for optimal image size:

1. **Builder Stage**: Uses `maven:3.9-eclipse-temurin-21-alpine` to compile the application
2. **Runtime Stage**: Uses `eclipse-temurin:21-jre-alpine` for a minimal runtime environment

### Image Size Optimization

- Base image: Alpine Linux (minimal footprint)
- Only JRE included (no full JDK)
- Build artifacts excluded via `.dockerignore`
- Final image size: ~250MB

## 🔧 Configuration

### Environment Variables

The following environment variables can be configured in `docker compose.yml`:

#### Database Configuration
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/blockmail
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: sakthivel
SPRING_JPA_HIBERNATE_DDL_AUTO: update
```

#### Server Configuration
```yaml
SERVER_PORT: 8080
SERVER_ADDRESS: 0.0.0.0
```

#### Security Configuration
```yaml
SPRING_SECURITY_JWT_SECRET_KEY: your-secret-key
SPRING_SECURITY_JWT_EXPIRATION_MS: 86400000
```

#### Email Configuration
```yaml
SPRING_MAIL_HOST: smtp.gmail.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: your-email@gmail.com
SPRING_MAIL_PASSWORD: your-app-password
```

#### CORS Configuration
```yaml
CLIENT_URLS: http://localhost:5173,http://localhost:5174
```

#### QKD Configuration
```yaml
QKD_KEYMANAGER_MOCK_MODE: true
QKD_KEYMANAGER_DEFAULT_KEY_SIZE: 256
QKD_KEYMANAGER_KEY_POOL_SIZE: 10
```

### Custom Configuration

To override default settings, edit the `docker compose.yml` file:

```yaml
environment:
  - SPRING_DATASOURCE_PASSWORD=your-custom-password
  - SERVER_PORT=8081
```

## 🏥 Health Checks

The container includes built-in health checks:

```bash
# Check container health status
sudo docker ps

# Manual health check
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## 📊 Monitoring & Logs

### View Logs

```bash
# All services
sudo docker compose logs -f

# Backend only
sudo docker compose logs -f quantum-mail-backend

# PostgreSQL only
sudo docker compose logs -f postgres

# Last 100 lines
sudo docker compose logs --tail=100 quantum-mail-backend
```

### Container Stats

```bash
# Real-time resource usage
sudo docker stats

# Specific container
sudo docker stats quantum-mail-backend
```

## 🔄 Managing the Application

### Start/Stop Services

```bash
# Start all services
sudo docker compose start

# Stop all services
sudo docker compose stop

# Restart all services
sudo docker compose restart

# Restart specific service
sudo docker compose restart quantum-mail-backend
```

### Rebuilding After Code Changes

```bash
# Rebuild and restart
sudo docker compose up -d --build

# Force recreate containers
sudo docker compose up -d --force-recreate
```

### Database Operations

```bash
# Access PostgreSQL CLI
sudo docker exec -it quantum-mail-postgres psql -U postgres -d blockmail

# Backup database
sudo docker exec quantum-mail-postgres pg_dump -U postgres blockmail > backup.sql

# Restore database
cat backup.sql | sudo docker exec -i quantum-mail-postgres psql -U postgres -d blockmail
```

## 🧹 Cleanup

### Remove Containers

```bash
# Stop and remove containers
sudo docker compose down

# Remove containers and volumes (WARNING: deletes all data)
sudo docker compose down -v

# Remove containers, volumes, and images
sudo docker compose down -v --rmi all
```

### Clean Docker System

```bash
# Remove unused containers, networks, images
sudo docker system prune -a

# Remove everything including volumes (DANGEROUS)
sudo docker system prune -a --volumes
```

## 🐛 Troubleshooting

### Container Won't Start

1. Check logs:
   ```bash
   sudo docker compose logs quantum-mail-backend
   ```

2. Verify port availability:
   ```bash
   sudo netstat -tulpn | grep 8080
   ```

3. Check PostgreSQL connection:
   ```bash
   sudo docker compose logs postgres
   ```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
sudo docker ps | grep postgres

# Test database connection
sudo docker exec quantum-mail-postgres pg_isready -U postgres

# Check network connectivity
sudo docker network inspect quantum-mail-network
```

### Memory Issues

If the container crashes due to memory:

1. Increase container memory in `docker compose.yml`:
   ```yaml
   deploy:
     resources:
       limits:
         memory: 1G
   ```

2. Adjust JVM heap size:
   ```yaml
   environment:
     - JAVA_OPTS=-Xmx768m -Xms384m
   ```

### Permission Issues

If you encounter permission errors:

```bash
# Give ownership to current user
sudo chown -R $USER:$USER .

# Make scripts executable
chmod +x mvnw
```

## 🔒 Security Considerations

### Production Deployment

For production environments:

1. **Change Default Passwords**:
   ```yaml
   environment:
     - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
     - SPRING_SECURITY_JWT_SECRET_KEY=${JWT_SECRET}
   ```

2. **Use Environment Files**:
   ```bash
   # Create .env file
   echo "DB_PASSWORD=secure-password" > .env
   echo "JWT_SECRET=secure-secret-key" >> .env
   
   # Docker Compose will automatically load .env
   ```

3. **Enable HTTPS**: Use a reverse proxy (nginx, traefik) with SSL certificates

4. **Limit Resource Usage**:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 1G
   ```

5. **Use Docker Secrets**: For sensitive data in swarm mode

## 📡 API Testing

After deployment, test the API:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register user
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 🌐 Network Configuration

The Docker Compose setup creates a custom bridge network `quantum-mail-network` that allows containers to communicate securely.

### Port Mapping

- **8080**: Backend API (HTTP)
- **5432**: PostgreSQL Database

### Accessing from Host

- Backend: http://localhost:8080
- Database: postgresql://localhost:5432/blockmail

### Accessing from Other Containers

- Backend: http://quantum-mail-backend:8080
- Database: postgresql://postgres:5432/blockmail

## 📈 Performance Tuning

### JVM Tuning

Optimize JVM settings in `docker compose.yml`:

```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Database Connection Pool

Adjust in application.properties:
```properties
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

## 🆘 Support

If you encounter issues:

1. Check the logs: `sudo docker compose logs -f`
2. Verify environment variables
3. Ensure ports are available
4. Check Docker daemon status: `sudo systemctl status docker`

## 📝 License

This Docker configuration is part of the Quantum Mail Backend project.

