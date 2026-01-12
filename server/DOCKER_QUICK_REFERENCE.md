# 🐳 Docker Quick Reference - Quantum Mail Backend

## 📋 Quick Start

### Using Helper Scripts (Recommended)

```bash
# Deploy application
./docker-deploy.sh dev          # Development mode
./docker-deploy.sh prod         # Production mode

# Manage services
./docker-manage.sh start        # Start services
./docker-manage.sh stop         # Stop services
./docker-manage.sh restart      # Restart services
./docker-manage.sh logs         # View logs
./docker-manage.sh status       # Check status
./docker-manage.sh clean        # Clean up
```

### Manual Commands

```bash
# Start everything
sudo docker-compose up -d

# Stop everything
sudo docker-compose down

# View logs
sudo docker-compose logs -f
```

## 🔧 Common Commands

### Container Management

```bash
# List running containers
sudo docker ps

# List all containers (including stopped)
sudo docker ps -a

# Start/Stop specific service
sudo docker-compose start quantum-mail-backend
sudo docker-compose stop quantum-mail-backend

# Restart specific service
sudo docker-compose restart quantum-mail-backend

# Remove containers
sudo docker-compose down
```

### Logs and Debugging

```bash
# View all logs
sudo docker compose logs

# Follow logs in real-time
sudo docker compose logs -f

# Backend logs only
sudo docker compose logs -f quantum-mail-backend

# Last 100 lines
sudo docker compose logs --tail=100

# Execute command inside container
sudo docker exec -it quantum-mail-backend bash
```

### Database Operations

```bash
# Access PostgreSQL
sudo docker exec -it quantum-mail-postgres psql -U postgres -d blockmail

# Backup database
sudo docker exec quantum-mail-postgres pg_dump -U postgres blockmail > backup_$(date +%Y%m%d).sql

# Restore database
cat backup.sql | sudo docker exec -i quantum-mail-postgres psql -U postgres -d blockmail

# View database logs
sudo docker compose logs postgres
```

### Image Management

```bash
# List images
sudo docker images

# Build image
sudo docker build -t quantum-mail-backend:latest .

# Remove image
sudo docker rmi quantum-mail-backend:latest

# Pull latest base images
sudo docker compose pull
```

### Rebuild and Deploy

```bash
# Rebuild and restart
sudo docker compose up -d --build

# Force recreate containers
sudo docker compose up -d --force-recreate

# Rebuild without cache
sudo docker compose build --no-cache
```

### Monitoring

```bash
# Real-time resource usage
sudo docker stats

# Container health status
sudo docker ps --format "table {{.Names}}\t{{.Status}}"

# Inspect container
sudo docker inspect quantum-mail-backend

# View container processes
sudo docker top quantum-mail-backend
```

### Cleanup

```bash
# Stop and remove containers
sudo docker compose down

# Remove containers and volumes (WARNING: deletes data)
sudo docker compose down -v

# Remove unused images
sudo docker image prune -a

# Remove everything unused
sudo docker system prune -a --volumes
```

## 🏥 Health Checks

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check database connection
sudo docker exec quantum-mail-postgres pg_isready -U postgres

# Check logs for errors
sudo docker compose logs | grep -i error
```

## 🔍 Troubleshooting

### Container won't start

```bash
# Check logs
sudo docker compose logs quantum-mail-backend

# Check all processes
sudo docker ps -a

# Verify ports
sudo netstat -tulpn | grep 8080
```

### Database connection issues

```bash
# Test PostgreSQL
sudo docker exec quantum-mail-postgres pg_isready -U postgres

# Check network
sudo docker network ls
sudo docker network inspect quantum-mail-network

# Restart database
sudo docker compose restart postgres
```

### Memory/Performance issues

```bash
# Check resource usage
sudo docker stats

# View container info
sudo docker inspect quantum-mail-backend | grep -A 10 Memory
```

## 📁 File Locations

```
├── Dockerfile                  # Main Dockerfile
├── docker-compose.yml          # Development compose
├── docker-compose.prod.yml     # Production compose
├── .dockerignore              # Files to exclude
├── .env.example               # Environment variables template
├── docker-deploy.sh           # Deployment script
├── docker-manage.sh           # Management script
└── DOCKER_DEPLOYMENT.md       # Full documentation
```

## 🌐 Access Points

- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Database**: postgresql://localhost:5432/blockmail
- **API Docs**: http://localhost:8080/api-docs (if enabled)

## 💡 Tips

1. **Use scripts**: `docker-deploy.sh` and `docker-manage.sh` are easier than manual commands
2. **Check logs first**: Most issues can be diagnosed from logs
3. **Health checks**: Always verify health endpoint after deployment
4. **Resource limits**: Monitor with `docker stats` to prevent resource issues
5. **Backups**: Regular database backups before major changes
6. **Environment variables**: Use `.env` file for configuration
7. **Production**: Always use `docker-compose.prod.yml` for production

## 🔐 Security Notes

- Change default passwords in `.env`
- Use strong JWT secret keys
- Limit exposed ports in production
- Use Docker secrets for sensitive data
- Regular security updates: `docker-compose pull`

## 📚 More Help

- Full documentation: `DOCKER_DEPLOYMENT.md`
- Docker docs: https://docs.docker.com/
- Compose reference: https://docs.docker.com/compose/compose-file/

