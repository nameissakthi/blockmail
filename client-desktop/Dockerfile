# Quantum Mail Desktop Client - Electron Application
FROM node:20-alpine

# Install pnpm and required dependencies for Electron
RUN npm install -g pnpm@9 && \
    apk add --no-cache \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ca-certificates \
    ttf-freefont \
    xvfb \
    dbus \
    udev \
    mesa-gl \
    gtk+3.0 \
    libxcomposite \
    libxcursor \
    libxdamage \
    libxi \
    libxtst \
    alsa-lib \
    at-spi2-core

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json pnpm-lock.yaml ./

# Install all dependencies (including dev dependencies for Electron)
RUN pnpm install --frozen-lockfile

# Copy application source code and configuration
COPY . .

# Set environment variables
ENV DISPLAY=:99
ENV NODE_ENV=production
ENV VITE_API_BASE_URL=http://localhost:8080
ENV ELECTRON_DISABLE_SANDBOX=1

# Expose port for Vite dev server
EXPOSE 5173

# Create startup script
RUN echo '#!/bin/sh\n\
echo "Starting Quantum Mail Desktop Client..."\n\
Xvfb :99 -screen 0 1920x1080x24 &\n\
sleep 2\n\
pnpm start\n\
' > /app/start.sh && chmod +x /app/start.sh

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD pgrep -f "node" || exit 1

# Run the application
CMD ["/app/start.sh"]
