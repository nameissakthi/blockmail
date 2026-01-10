#!/bin/bash

# Quantum Mail - Start Frontend (Electron App)
# This script starts the Electron desktop application

echo "🚀 Starting Quantum Mail Frontend (Electron)..."
echo "=============================================="

# Navigate to client-desktop directory
cd "$(dirname "$0")"

# Check if backend is running
if ! curl -s http://localhost:8080/login > /dev/null 2>&1; then
    echo "⚠️  WARNING: Backend server is not running!"
    echo "Please start the backend first:"
    echo "  cd ../server && ./start-backend-fixed.sh"
    echo ""
    echo "Continue anyway? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo "✅ Backend server is running"
fi

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    pnpm install
    
    if [ $? -ne 0 ]; then
        echo "❌ Installation failed! Please check the errors above."
        exit 1
    fi
fi

echo "✅ Dependencies are installed"

# Check .env file
if [ ! -f ".env" ]; then
    echo "⚠️  WARNING: .env file not found!"
    echo "Creating .env from .env.example..."
    cp .env.example .env
fi

echo "✅ Environment configuration found"

# Start Electron app
echo "🔥 Starting Electron application..."
echo "The Quantum Mail desktop app will open shortly..."
echo "Press Ctrl+C in this terminal to stop the app"
echo ""

pnpm start
