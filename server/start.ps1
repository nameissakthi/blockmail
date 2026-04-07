Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

Write-Host "=============================================="
Write-Host "Quantum Mail Backend - Windows Start Script"
Write-Host "=============================================="

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker CLI was not found. Install Docker Desktop and try again."
}

Write-Host "[1/2] Checking PostgreSQL container..."
$postgresRunning = docker ps --format "{{.Names}}" | Select-String -Pattern "^postgres$" -Quiet
if (-not $postgresRunning) {
    Write-Host "PostgreSQL container 'postgres' is not running. Starting it..."
    docker start postgres | Out-Null
    Write-Host "PostgreSQL started."
} else {
    Write-Host "PostgreSQL is already running."
}

Write-Host "[2/2] Starting Spring Boot application..."
& ".\mvnw.cmd" "spring-boot:run"
