#!/bin/bash

###############################################################################
# Quantum Mail Backend - Docker Management Script
# Usage: ./docker-manage.sh [start|stop|restart|logs|status|clean]
###############################################################################

set -e

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

print_banner() {
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║     Quantum Mail Backend - Docker Manager             ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Start services
start_services() {
    print_info "Starting Quantum Mail services..."
    sudo docker compose up -d
    print_success "Services started"
    show_status
}

# Stop services
stop_services() {
    print_info "Stopping Quantum Mail services..."
    sudo docker compose stop
    print_success "Services stopped"
}

# Restart services
restart_services() {
    print_info "Restarting Quantum Mail services..."
    sudo docker compose restart
    print_success "Services restarted"
    show_status
}

# Show logs
show_logs() {
    print_info "Showing logs (press Ctrl+C to exit)..."
    sudo docker compose logs -f
}

# Show status
show_status() {
    echo ""
    print_info "Container Status:"
    sudo docker ps --filter "name=quantum-mail" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || true
    echo ""

    # Check health
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        print_success "Backend Health: UP"
    else
        print_warning "Backend Health: DOWN or Starting..."
    fi
    echo ""
}

# Clean up
clean_services() {
    print_warning "This will remove all containers and volumes. Continue? (y/N)"
    read -r response

    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_info "Cleaning up containers and volumes..."
        sudo docker compose down -v
        print_success "Cleanup completed"
    else
        print_info "Cleanup cancelled"
    fi
}

# Show help
show_help() {
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  start     - Start all services"
    echo "  stop      - Stop all services"
    echo "  restart   - Restart all services"
    echo "  logs      - Show and follow logs"
    echo "  status    - Show container status"
    echo "  clean     - Remove containers and volumes"
    echo "  help      - Show this help message"
    echo ""
}

# Main
main() {
    print_banner

    case "${1:-help}" in
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        logs)
            show_logs
            ;;
        status)
            show_status
            ;;
        clean)
            clean_services
            ;;
        help|*)
            show_help
            ;;
    esac
}

main "$@"

