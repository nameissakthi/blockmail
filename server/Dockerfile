# Multi-stage Dockerfile for Quantum Mail Backend with Blockchain Integration
# Stage 1: Blockchain Smart Contract Compilation
FROM node:20-alpine AS blockchain-builder

WORKDIR /blockchain

# Copy blockchain-related files
COPY package*.json ./
COPY hardhat.config.js ./
COPY contracts/ ./contracts/
COPY scripts/ ./scripts/

# Install Node.js dependencies (including dev dependencies for Hardhat)
RUN npm install && \
    npx hardhat compile

# Stage 2: Java Application Build
FROM maven:3.9-eclipse-temurin-21-alpine AS java-builder

WORKDIR /app

# Copy Maven files for dependency resolution
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 3: Runtime image with blockchain support
FROM eclipse-temurin:21-jre-jammy

# Install Node.js, curl, and other dependencies
RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring

# Set working directory
WORKDIR /app

# Copy compiled smart contracts and blockchain artifacts from blockchain-builder
COPY --from=blockchain-builder /blockchain/artifacts ./blockchain/artifacts
COPY --from=blockchain-builder /blockchain/cache ./blockchain/cache
COPY --from=blockchain-builder /blockchain/node_modules ./blockchain/node_modules
COPY --from=blockchain-builder /blockchain/package.json ./blockchain/

# Copy blockchain configuration and scripts
COPY hardhat.config.js ./blockchain/
COPY contracts/ ./blockchain/contracts/
COPY scripts/ ./blockchain/scripts/

# Copy the built JAR from java-builder
COPY --from=java-builder /app/target/server-0.0.1-SNAPSHOT.jar app.jar


# Change ownership to spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for container environment
ENV JAVA_OPTS="-Xmx768m -Xms384m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

