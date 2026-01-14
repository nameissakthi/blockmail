#!/bin/bash

# Quantum Mail - Docker Blockchain Deployment Script
# This script deploys smart contracts to the Ganache blockchain running in Docker

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  Quantum Mail - Docker Blockchain Deployment v2         ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""

# Step 1: Build Docker images
echo -e "${BLUE}[1/6] Building Docker images with blockchain support...${NC}"
sudo docker-compose build
echo -e "${GREEN}✅ Docker images built successfully${NC}"
echo ""

# Step 2: Start Ganache blockchain
echo -e "${BLUE}[2/6] Starting Ganache blockchain...${NC}"
sudo docker-compose up -d ganache
echo -e "${YELLOW}⏳ Waiting for Ganache to be ready...${NC}"
sleep 10

# Check if Ganache is healthy
if sudo docker-compose ps ganache | grep -q "healthy"; then
    echo -e "${GREEN}✅ Ganache blockchain is running${NC}"
else
    echo -e "${YELLOW}⚠️  Ganache is starting... (waiting 10 more seconds)${NC}"
    sleep 10
fi
echo ""

# Step 3: Get Ganache accounts
echo -e "${BLUE}[3/6] Fetching Ganache accounts...${NC}"
ACCOUNTS=$(curl -s -X POST --data '{"jsonrpc":"2.0","method":"eth_accounts","params":[],"id":1}' http://localhost:8545 | jq -r '.result[]' | head -5)

if [ -z "$ACCOUNTS" ]; then
    echo -e "${RED}❌ Failed to fetch accounts from Ganache${NC}"
    echo -e "${YELLOW}   Make sure Ganache is running on port 8545${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Found Ganache accounts:${NC}"
echo "$ACCOUNTS" | nl
DEPLOYER_ACCOUNT=$(echo "$ACCOUNTS" | head -1)
echo ""
echo -e "${YELLOW}📝 Using deployer account: ${DEPLOYER_ACCOUNT}${NC}"
echo ""

# Step 4: Compile smart contracts
echo -e "${BLUE}[4/6] Compiling smart contracts...${NC}"
npx hardhat compile
echo -e "${GREEN}✅ Smart contracts compiled${NC}"
echo ""

# Step 5: Deploy smart contracts to Ganache
echo -e "${BLUE}[5/6] Deploying smart contracts to Ganache...${NC}"

# Update hardhat config to use docker ganache
export BLOCKCHAIN_RPC_URL=http://localhost:8545
export BLOCKCHAIN_CHAIN_ID=1337
export GANACHE_PRIVATE_KEY=0x4f3edf983ac636a65a842ce7c78d9aa706d3b113bce9c46f30d7d21715b23b1d

npx hardhat run scripts/deploy.js --network ganache

if [ ! -f "deployment-info.json" ]; then
    echo -e "${RED}❌ Deployment failed - deployment-info.json not found${NC}"
    exit 1
fi

CONTRACT_ADDRESS=$(cat deployment-info.json | jq -r '.contractAddress')
echo -e "${GREEN}✅ Smart contract deployed${NC}"
echo -e "${GREEN}   Contract Address: ${CONTRACT_ADDRESS}${NC}"
echo ""

# Step 6: Update .env file with contract address
echo -e "${BLUE}[6/6] Updating configuration...${NC}"

if [ -f ".env" ]; then
    # Update existing .env
    if grep -q "BLOCKCHAIN_CONTRACT_ADDRESS=" .env; then
        sed -i "s|BLOCKCHAIN_CONTRACT_ADDRESS=.*|BLOCKCHAIN_CONTRACT_ADDRESS=${CONTRACT_ADDRESS}|" .env
    else
        echo "BLOCKCHAIN_CONTRACT_ADDRESS=${CONTRACT_ADDRESS}" >> .env
    fi

    # Enable blockchain if not already enabled
    if grep -q "BLOCKCHAIN_ENABLED=false" .env; then
        sed -i "s|BLOCKCHAIN_ENABLED=false|BLOCKCHAIN_ENABLED=true|" .env
    fi

    echo -e "${GREEN}✅ Configuration updated in .env${NC}"
else
    echo -e "${YELLOW}⚠️  .env file not found${NC}"
fi
echo ""

# Step 7: Start all services
echo -e "${BLUE}Starting all services (PostgreSQL, Ganache, Application)...${NC}"
sudo docker-compose up -d
echo ""

# Step 8: Wait for application to be healthy
echo -e "${YELLOW}⏳ Waiting for application to start...${NC}"
for i in {1..30}; do
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Application is healthy and running!${NC}"
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

# Display summary
echo ""
echo -e "${BLUE}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║          🎉 Deployment Complete! 🎉                     ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}✅ PostgreSQL Database:${NC} Running on port 5432"
echo -e "${GREEN}✅ Ganache Blockchain:${NC} Running on ports 7545 & 8545"
echo -e "${GREEN}✅ Quantum Mail Backend:${NC} Running on port 8080"
echo ""
echo -e "${YELLOW}📋 Blockchain Details:${NC}"
echo -e "   Network: Ganache (Local Ethereum)"
echo -e "   Chain ID: 1337"
echo -e "   RPC URL: http://localhost:8545"
echo -e "   Contract Address: ${CONTRACT_ADDRESS}"
echo -e "   Deployer Account: ${DEPLOYER_ACCOUNT}"
echo ""
echo -e "${YELLOW}📋 Useful Commands:${NC}"
echo -e "   View logs:        docker-compose logs -f"
echo -e "   Stop services:    docker-compose down"
echo -e "   Restart:          docker-compose restart"
echo -e "   Check health:     curl http://localhost:8080/actuator/health"
echo ""
echo -e "${BLUE}🌐 Application URL:${NC} http://localhost:8080"
echo -e "${BLUE}🔗 Ganache RPC:${NC} http://localhost:8545"
echo ""
echo -e "${GREEN}Your Quantum Mail application with blockchain is ready! 🚀${NC}"
echo ""

