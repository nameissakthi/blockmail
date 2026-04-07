# Quantum Mail Application

Quantum Mail Application is a full-stack secure mail project with:

- a Spring Boot backend for authentication, users, and mail APIs
- an Electron frontend for the desktop client
- PostgreSQL for local data storage

## Run Locally

This project is configured for a Windows machine with local PostgreSQL installed.

### Backend

Open a new terminal:

```powershell
cd server
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

### Frontend

Open another new terminal:

```powershell
cd client-desktop
pnpm start
```

The frontend uses Electron Forge and starts the desktop app with the Vite dev server.

## Local Setup Notes

- PostgreSQL should be running locally.
- The backend uses the database configured in `server/.env`.
- Blockchain is disabled for local run on this system.
- If `mvn` does not work in an older terminal, close it and open a fresh terminal.
