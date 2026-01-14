#!/bin/bash

# Quantum Mail - Blockchain Setup Script
# This script helps you set up real blockchain integration with Ganache

set -e

echo "╔══════════════════════════════════════════════════════════╗"
echo "║   Quantum Mail - Real Blockchain Integration Setup      ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if Ganache is running
check_ganache() {
    echo -e "${BLUE}[1/7] Checking Ganache connection...${NC}"
    if curl -s -X POST --data '{"jsonrpc":"2.0","method":"eth_blockNumber","params":[],"id":1}' http://127.0.0.1:7545 > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Ganache is running on http://127.0.0.1:7545${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠️  Ganache is not running on port 7545${NC}"
        echo -e "${YELLOW}   Please start Ganache and make sure it's running on http://127.0.0.1:7545${NC}"
        echo -e "${YELLOW}   Or update the RPC URL in hardhat.config.js if using a different port${NC}"
        read -p "Press Enter after starting Ganache, or Ctrl+C to exit..."
        check_ganache
    fi
}

# Install Node dependencies
install_dependencies() {
    echo -e "\n${BLUE}[2/7] Installing Node.js dependencies...${NC}"
    if [ ! -f "package.json" ]; then
        echo -e "${RED}❌ package.json not found${NC}"
        exit 1
    fi
    npm install
    echo -e "${GREEN}✅ Dependencies installed${NC}"
}

# Get Ganache accounts
get_ganache_accounts() {
    echo -e "\n${BLUE}[3/7] Fetching Ganache accounts...${NC}"

    # Fetch accounts from Ganache
    ACCOUNTS=$(curl -s -X POST --data '{"jsonrpc":"2.0","method":"eth_accounts","params":[],"id":1}' http://127.0.0.1:7545 | jq -r '.result[]' | head -5)

    if [ -z "$ACCOUNTS" ]; then
        echo -e "${RED}❌ Failed to fetch accounts from Ganache${NC}"
        exit 1
    fi

    echo -e "${GREEN}✅ Found Ganache accounts:${NC}"
    echo "$ACCOUNTS" | nl

    # Save first account for later use
    DEPLOYER_ACCOUNT=$(echo "$ACCOUNTS" | head -1)
    echo ""
    echo -e "${YELLOW}📝 Note: You need to add private keys to hardhat.config.js${NC}"
    echo -e "${YELLOW}   Open Ganache GUI, click the key icon next to the first account,${NC}"
    echo -e "${YELLOW}   copy the private key, and update hardhat.config.js${NC}"
    echo ""
    read -p "Have you updated hardhat.config.js with the private key? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}⚠️  Please update hardhat.config.js and run this script again${NC}"
        exit 1
    fi
}

# Compile smart contracts
compile_contracts() {
    echo -e "\n${BLUE}[4/7] Compiling smart contracts...${NC}"
    npx hardhat compile
    echo -e "${GREEN}✅ Contracts compiled${NC}"
}

# Deploy smart contracts
deploy_contracts() {
    echo -e "\n${BLUE}[5/7] Deploying contracts to Ganache...${NC}"
    npx hardhat run scripts/deploy.js --network ganache

    if [ ! -f "deployment-info.json" ]; then
        echo -e "${RED}❌ Deployment failed - deployment-info.json not found${NC}"
        exit 1
    fi

    CONTRACT_ADDRESS=$(cat deployment-info.json | jq -r '.contractAddress')
    echo -e "${GREEN}✅ Contract deployed at: ${CONTRACT_ADDRESS}${NC}"
}

# Update .env file with contract address
update_env_file() {
    echo -e "\n${BLUE}[6/7] Updating .env file with contract address...${NC}"

    if [ ! -f ".env" ]; then
        echo -e "${YELLOW}⚠️  .env file not found, creating from .env.example${NC}"
        cp .env.example .env
    fi

    if [ ! -f "deployment-info.json" ]; then
        echo -e "${RED}❌ deployment-info.json not found${NC}"
        exit 1
    fi

    CONTRACT_ADDRESS=$(cat deployment-info.json | jq -r '.contractAddress')

    echo -e "${YELLOW}📝 Updating contract address: ${CONTRACT_ADDRESS}${NC}"

    # Update contract address in .env
    if grep -q "BLOCKCHAIN_CONTRACT_ADDRESS=" .env; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|BLOCKCHAIN_CONTRACT_ADDRESS=.*|BLOCKCHAIN_CONTRACT_ADDRESS=${CONTRACT_ADDRESS}|" .env
        else
            sed -i "s|BLOCKCHAIN_CONTRACT_ADDRESS=.*|BLOCKCHAIN_CONTRACT_ADDRESS=${CONTRACT_ADDRESS}|" .env
        fi
    else
        echo "BLOCKCHAIN_CONTRACT_ADDRESS=${CONTRACT_ADDRESS}" >> .env
    fi

    # Enable blockchain if not already enabled
    if grep -q "BLOCKCHAIN_ENABLED=false" .env; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|BLOCKCHAIN_ENABLED=false|BLOCKCHAIN_ENABLED=true|" .env
        else
            sed -i "s|BLOCKCHAIN_ENABLED=false|BLOCKCHAIN_ENABLED=true|" .env
        fi
    fi

    echo -e "${GREEN}✅ .env file updated${NC}"
}

# Install Maven dependencies and compile
maven_build() {
    echo -e "\n${BLUE}[7/7] Building Java project with Maven...${NC}"
    ./mvnw clean compile -DskipTests
    echo -e "${GREEN}✅ Maven build completed${NC}"
}

# Main execution
main() {
    echo -e "${BLUE}Starting blockchain setup...${NC}\n"

    check_ganache
    install_dependencies
    get_ganache_accounts
    compile_contracts
    deploy_contracts
    update_env_file
    maven_build

    echo ""
    echo "╔══════════════════════════════════════════════════════════╗"
    echo "║          🎉 Blockchain Setup Complete! 🎉               ║"
    echo "╚══════════════════════════════════════════════════════════╝"
    echo ""
    echo -e "${GREEN}✅ Smart contract deployed to Ganache${NC}"
    echo -e "${GREEN}✅ Java backend configured for blockchain integration${NC}"
    echo ""
    echo -e "${YELLOW}📋 Next Steps:${NC}"
    echo "   1. Review deployment-info.json for contract details"
    echo "   2. Start your Spring Boot application"
    echo "   3. All emails and key audits will now be recorded on blockchain!"
    echo ""
    echo -e "${BLUE}Contract Address:${NC} $(cat deployment-info.json | jq -r '.contractAddress')"
    echo -e "${BLUE}Network:${NC} Ganache (Local Ethereum)"
    echo -e "${BLUE}RPC URL:${NC} http://127.0.0.1:7545"
    echo ""
}

# Run main function
main

