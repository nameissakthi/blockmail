#!/bin/bash

###############################################################################
# Quantum Mail Backend - Docker Build and Deploy Script
# Usage: ./docker-deploy.sh [dev|prod]
###############################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored messages
print_info() {
    echo -e "${BLUE}ℹ ${1}${NC}"
}

print_success() {
    echo -e "${GREEN}✓ ${1}${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ ${1}${NC}"
}

print_error() {
    echo -e "${RED}✗ ${1}${NC}"
}

# Print banner
print_banner() {
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║     Quantum Mail Backend - Docker Deployment          ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Check if Docker is installed
check_docker() {
    print_info "Checking Docker installation..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    print_success "Docker is installed: $(docker --version)"
}

# Check if Docker Compose is installed
check_docker_compose() {
    print_info "Checking Docker Compose installation..."
    if ! sudo docker compose version &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    print_success "Docker Compose is installed: $(sudo docker compose version)"
}

# Stop existing containers
stop_containers() {
    print_info "Stopping existing containers..."
    if [ "$ENV" = "prod" ]; then
        sudo docker compose -f docker-compose.prod.yml down 2>/dev/null || true
    else
        sudo docker compose down 2>/dev/null || true
    fi
    print_success "Containers stopped"
}

# Build Docker image
build_image() {
    print_info "Building Docker image..."
    sudo docker build -t quantum-mail-backend:latest .
    print_success "Docker image built successfully"
}

# Deploy with Docker Compose
deploy() {
    print_info "Deploying Quantum Mail Backend ($ENV mode)..."

    if [ "$ENV" = "prod" ]; then
        # Check for .env file in production
        if [ ! -f .env ]; then
            print_warning ".env file not found. Creating from .env.example..."
            cp .env.example .env
            print_warning "Please update .env file with production values before deploying!"
            exit 1
        fi

        sudo docker compose -f docker-compose.prod.yml up -d --build
    else
        sudo docker compose up -d --build
    fi

    print_success "Deployment completed"
}

# Wait for services to be healthy
wait_for_health() {
    print_info "Waiting for services to be healthy..."

    max_attempts=30
    attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            print_success "Backend is healthy!"
            return 0
        fi

        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done

    print_error "Backend health check failed after ${max_attempts} attempts"
    print_info "Checking logs..."
    sudo docker-compose logs --tail=50 quantum-mail-backend
    exit 1
}

# Show deployment info
show_info() {
    echo ""
    print_success "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    print_success "  Quantum Mail Backend Deployed Successfully!"
    print_success "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    print_info "Backend API: http://localhost:8080"
    print_info "Health Check: http://localhost:8080/actuator/health"
    print_info "Database: postgresql://localhost:5432/blockmail"
    echo ""
    print_info "Useful Commands:"
    echo "  • View logs: sudo docker compose logs -f"
    echo "  • Stop services: sudo docker compose down"
    echo "  • Restart: sudo docker compose restart"
    echo "  • View stats: sudo docker stats"
    echo ""

    # Show container status
    print_info "Container Status:"
    sudo docker ps --filter "name=quantum-mail" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
}

# Main script
main() {
    print_banner

    # Get environment (default to dev)
    ENV=${1:-dev}

    if [ "$ENV" != "dev" ] && [ "$ENV" != "prod" ]; then
        print_error "Invalid environment. Use 'dev' or 'prod'"
        echo "Usage: $0 [dev|prod]"
        exit 1
    fi

    print_info "Deploying in ${ENV} mode..."

    # Pre-flight checks
    check_docker
    check_docker_compose

    # Deploy
    stop_containers
    build_image
    deploy

    # Wait for services
    sleep 5
    wait_for_health

    # Show info
    show_info
}

# Run main function
main "$@"

