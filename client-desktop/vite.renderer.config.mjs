import { defineConfig } from 'vite';

// Dev server proxy to forward API calls to backend and avoid CORS in development
export default defineConfig(({ mode }) => {
  const isDev = mode === 'development' || process.env.VITE_DEV_MODE === 'true';

  return {
    server: isDev
      ? {
          port: 5173,
          proxy: {
            // Proxy any path that starts with /api to the backend
            '/api': {
              target: 'http://localhost:8080',
              changeOrigin: true,
              secure: false,
              // keep path as-is
              rewrite: (path) => path
            },
            // Also proxy auth endpoints that are not under /api
            '/register': {
              target: 'http://localhost:8080',
              changeOrigin: true,
              secure: false
            },
            '/login': {
              target: 'http://localhost:8080',
              changeOrigin: true,
              secure: false
            },
            '/logout': {
              target: 'http://localhost:8080',
              changeOrigin: true,
              secure: false
            }
          }
        }
      : undefined
  };
});
