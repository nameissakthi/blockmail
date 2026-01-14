#!/bin/bash

# Quantum Mail Backend - Start Script
# This script starts the Quantum Mail backend application

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "╔══════════════════════════════════════════════════════════╗"
echo "║        Quantum Mail Backend - Starting Server           ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# Check if PostgreSQL is running
echo -e "${BLUE}[1/3] Checking PostgreSQL...${NC}"
if sudo docker ps | grep -q postgres; then
    echo -e "${GREEN}✅ PostgreSQL is running${NC}"
else
    echo -e "${YELLOW}⚠️  PostgreSQL is not running. Starting...${NC}"
    sudo docker start postgres || {
        echo -e "${RED}❌ Failed to start PostgreSQL${NC}"
        echo -e "${YELLOW}Run: sudo docker start postgres${NC}"
        exit 1
    }
    sleep 2
    echo -e "${GREEN}✅ PostgreSQL started${NC}"
fi

# Check if application is already running
echo -e "\n${BLUE}[2/3] Checking for existing application...${NC}"
if pgrep -f "spring-boot:run" > /dev/null; then
    echo -e "${YELLOW}⚠️  Application is already running${NC}"
    read -p "Do you want to restart it? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${BLUE}Stopping existing application...${NC}"
        pkill -f "spring-boot:run"
        sleep 2
    else
        echo -e "${GREEN}Keeping existing application running${NC}"
        exit 0
    fi
fi

# Start the application
echo -e "\n${BLUE}[3/3] Starting Quantum Mail Backend...${NC}"
echo -e "${YELLOW}This may take a few moments...${NC}"
echo ""

./mvnw spring-boot:run

# If script reaches here, application has stopped
echo -e "\n${BLUE}Application stopped${NC}"

