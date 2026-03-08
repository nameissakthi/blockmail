#!/bin/bash

# Quantum Mail Server - Docker Hub Push Script
# Author: Sakthivel
# Date: March 8, 2026

echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║         Docker Hub Push - Quantum Mail Server v1                  ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Docker Hub credentials
DOCKER_USERNAME="sakthiveldk"
IMAGE_NAME="quantum-mail-server"
VERSION="v1"

echo -e "${BLUE}[1/4] Checking Docker Hub login status...${NC}"
if sudo docker info | grep -q "Username: $DOCKER_USERNAME"; then
    echo -e "${GREEN}✅ Already logged in as $DOCKER_USERNAME${NC}"
else
    echo -e "${YELLOW}⚠️  Not logged in. Please login to Docker Hub:${NC}"
    sudo docker login
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Docker Hub login failed. Exiting.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Successfully logged in${NC}"
fi

echo ""
echo -e "${BLUE}[2/4] Verifying Docker images...${NC}"
if sudo docker images | grep -q "$DOCKER_USERNAME/$IMAGE_NAME"; then
    echo -e "${GREEN}✅ Images found:${NC}"
    sudo docker images | grep "$DOCKER_USERNAME/$IMAGE_NAME"
else
    echo -e "${RED}❌ Images not found. Please build first.${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}[3/4] Pushing to Docker Hub...${NC}"
echo -e "${YELLOW}This may take several minutes (image size: 1.45GB)${NC}"
echo ""

# Push version tag
echo -e "${BLUE}Pushing $DOCKER_USERNAME/$IMAGE_NAME:$VERSION...${NC}"
sudo docker push $DOCKER_USERNAME/$IMAGE_NAME:$VERSION
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Successfully pushed $VERSION tag${NC}"
else
    echo -e "${RED}❌ Failed to push $VERSION tag${NC}"
    exit 1
fi

echo ""

# Push latest tag
echo -e "${BLUE}Pushing $DOCKER_USERNAME/$IMAGE_NAME:latest...${NC}"
sudo docker push $DOCKER_USERNAME/$IMAGE_NAME:latest
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Successfully pushed latest tag${NC}"
else
    echo -e "${RED}❌ Failed to push latest tag${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}[4/4] Verification...${NC}"
echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                  🎉 PUSH SUCCESSFUL! 🎉                            ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}📦 Docker Hub Repository:${NC}"
echo "   https://hub.docker.com/r/$DOCKER_USERNAME/$IMAGE_NAME"
echo ""
echo -e "${BLUE}🐳 Pull Commands:${NC}"
echo "   docker pull $DOCKER_USERNAME/$IMAGE_NAME:$VERSION"
echo "   docker pull $DOCKER_USERNAME/$IMAGE_NAME:latest"
echo ""
echo -e "${BLUE}📖 Deployment Guide:${NC}"
echo "   See DOCKER_HUB_DEPLOYMENT.md for deployment options"
echo ""
echo -e "${GREEN}✅ Your image is now publicly available on Docker Hub!${NC}"
echo ""

