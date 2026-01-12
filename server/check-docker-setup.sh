#!/bin/bash

###############################################################################
# Docker Installation Check & Setup for Quantum Mail Backend
# This script checks if Docker and Docker Compose are properly installed
###############################################################################

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() { echo -e "${BLUE}ℹ ${1}${NC}"; }
print_success() { echo -e "${GREEN}✓ ${1}${NC}"; }
print_warning() { echo -e "${YELLOW}⚠ ${1}${NC}"; }
print_error() { echo -e "${RED}✗ ${1}${NC}"; }

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════╗"
echo "║   Docker Setup Verification - Quantum Mail Backend    ║"
echo "╚════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Check Docker
print_info "Checking Docker installation..."
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_success "Docker is installed: $DOCKER_VERSION"
else
    print_error "Docker is not installed"
    echo ""
    echo "To install Docker on Ubuntu/Debian:"
    echo "  curl -fsSL https://get.docker.com -o get-docker.sh"
    echo "  sudo sh get-docker.sh"
    echo ""
    exit 1
fi

# Check Docker Compose
print_info "Checking Docker Compose..."
if sudo docker compose version &> /dev/null; then
    COMPOSE_VERSION=$(sudo docker compose version)
    print_success "Docker Compose is installed: $COMPOSE_VERSION"
else
    print_error "Docker Compose is not available"
    echo ""
    echo "Docker Compose V2 is included with Docker Desktop"
    echo "For manual installation, see: https://docs.docker.com/compose/install/"
    echo ""
    exit 1
fi

# Check Docker service
print_info "Checking Docker service status..."
if sudo systemctl is-active --quiet docker; then
    print_success "Docker service is running"
else
    print_warning "Docker service is not running"
    print_info "Starting Docker service..."
    sudo systemctl start docker
    if sudo systemctl is-active --quiet docker; then
        print_success "Docker service started successfully"
    else
        print_error "Failed to start Docker service"
        exit 1
    fi
fi

# Check if user is in docker group
print_info "Checking Docker permissions..."
if groups $USER | grep &>/dev/null '\bdocker\b'; then
    print_success "User '$USER' is in docker group"
else
    print_warning "User '$USER' is not in docker group"
    echo ""
    echo "To add user to docker group (no sudo needed for docker commands):"
    echo "  sudo usermod -aG docker $USER"
    echo "  newgrp docker"
    echo ""
    echo "You can still use Docker with 'sudo' prefix"
fi

# Check if ports are available
print_info "Checking if required ports are available..."

PORT_8080_FREE=true
PORT_5432_FREE=true

if sudo netstat -tuln 2>/dev/null | grep -q ':8080 '; then
    print_warning "Port 8080 is already in use"
    PORT_8080_FREE=false
    echo "  Process using port 8080:"
    sudo netstat -tulpn | grep ':8080 ' | head -1
else
    print_success "Port 8080 is available"
fi

if sudo netstat -tuln 2>/dev/null | grep -q ':5432 '; then
    print_warning "Port 5432 is already in use (PostgreSQL)"
    PORT_5432_FREE=false
    echo "  Process using port 5432:"
    sudo netstat -tulpn | grep ':5432 ' | head -1
else
    print_success "Port 5432 is available"
fi

# Check disk space
print_info "Checking available disk space..."
AVAILABLE_SPACE=$(df -h . | awk 'NR==2 {print $4}')
print_success "Available disk space: $AVAILABLE_SPACE"

# Check if Docker files exist
print_info "Checking Docker configuration files..."
FILES_OK=true

if [ -f "Dockerfile" ]; then
    print_success "Dockerfile found"
else
    print_error "Dockerfile not found"
    FILES_OK=false
fi

if [ -f "docker-compose.yml" ]; then
    print_success "docker-compose.yml found"
else
    print_error "docker-compose.yml not found"
    FILES_OK=false
fi

if [ -x "docker-deploy.sh" ]; then
    print_success "docker-deploy.sh found and executable"
else
    print_warning "docker-deploy.sh not executable"
    chmod +x docker-deploy.sh 2>/dev/null || true
fi

if [ -x "docker-manage.sh" ]; then
    print_success "docker-manage.sh found and executable"
else
    print_warning "docker-manage.sh not executable"
    chmod +x docker-manage.sh 2>/dev/null || true
fi

# Summary
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                    SUMMARY                            ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}"
echo ""

ALL_OK=true

if command -v docker &> /dev/null && sudo docker compose version &> /dev/null && $FILES_OK; then
    print_success "All prerequisites are met!"
    echo ""
    print_info "You can now deploy the application:"
    echo ""
    echo "  For development:"
    echo "    ./docker-deploy.sh dev"
    echo ""
    echo "  For production (after configuring .env):"
    echo "    cp .env.example .env"
    echo "    nano .env"
    echo "    ./docker-deploy.sh prod"
    echo ""
    print_info "Or use the management script:"
    echo "    ./docker-manage.sh start"
    echo ""
else
    print_error "Some prerequisites are missing. Please resolve the issues above."
    ALL_OK=false
fi

if ! $PORT_8080_FREE || ! $PORT_5432_FREE; then
    print_warning "Warning: Some ports are in use. You may need to:"
    echo "  - Stop conflicting services"
    echo "  - Modify port mappings in docker-compose.yml"
    echo ""
fi

echo -e "${BLUE}═══════════════════════════════════════════════════════${NC}"
echo ""

if $ALL_OK; then
    exit 0
else
    exit 1
fi

