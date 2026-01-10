#!/bin/bash

# Quantum Mail - Start Backend Server
# This script starts the Spring Boot backend

echo "🚀 Starting Quantum Mail Backend Server..."
echo "========================================"

# Navigate to server directory
cd "$(dirname "$0")"

# Check if Docker PostgreSQL is running
echo "Checking Docker PostgreSQL..."

# Check if Docker is running
if ! sudo docker ps > /dev/null 2>&1; then
    echo "❌ ERROR: Docker is not running!"
    echo "Please start Docker first:"
    echo "  sudo systemctl start docker"
    exit 1
fi

# Check if PostgreSQL container is running (works with postgres, bitnami/postgresql, etc.)
POSTGRES_CONTAINER=$(sudo docker ps --format "{{.Names}}" | grep -i postgres | head -n 1)

if [ -z "$POSTGRES_CONTAINER" ]; then
    # Try to find stopped container
    POSTGRES_CONTAINER=$(sudo docker ps -a --format "{{.Names}}" | grep -i postgres | head -n 1)
    
    if [ -z "$POSTGRES_CONTAINER" ]; then
        echo "❌ ERROR: No PostgreSQL container found!"
        echo "Please start your PostgreSQL Docker container"
        exit 1
    else
        echo "Starting PostgreSQL container: $POSTGRES_CONTAINER"
        sudo docker start $POSTGRES_CONTAINER
        sleep 3
    fi
fi

echo "✅ PostgreSQL container is running: $POSTGRES_CONTAINER"

# Check if database exists using bitnami PostgreSQL credentials
DB_EXISTS=$(sudo docker exec -e PGPASSWORD=sakthivel $POSTGRES_CONTAINER psql -h localhost -U postgres -lqt 2>/dev/null | cut -d \| -f 1 | grep -w blockmail)

if [ -z "$DB_EXISTS" ]; then
    echo "⚠️  WARNING: Database 'blockmail' not found!"
    echo "Creating database..."
    sudo docker exec -e PGPASSWORD=sakthivel $POSTGRES_CONTAINER psql -h localhost -U postgres -c "CREATE DATABASE blockmail;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ Database created successfully"
    else
        echo "⚠️  Database may already exist"
    fi
fi

echo "✅ Database 'blockmail' exists"

# Clean and build
echo "📦 Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Please check the errors above."
    exit 1
fi

echo "✅ Build successful"

# Start the server
echo "🔥 Starting Spring Boot application..."
echo "Backend will be available at: http://localhost:8080"
echo "Press Ctrl+C to stop the server"
echo ""

./mvnw spring-boot:run
