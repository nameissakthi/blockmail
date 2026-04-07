Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

Write-Host "=============================================="
Write-Host "Quantum Mail Frontend - Windows Start Script"
Write-Host "=============================================="

Write-Host "Checking backend availability on http://localhost:8080 ..."
$backendOk = $false
try {
    $null = Invoke-WebRequest -Uri "http://localhost:8080/login" -UseBasicParsing -TimeoutSec 4
    $backendOk = $true
} catch {
    $backendOk = $false
}

if (-not $backendOk) {
    Write-Warning "Backend server is not reachable on http://localhost:8080"
    Write-Host "Start backend first from server folder using .\start.ps1"
    $choice = Read-Host "Continue anyway? (y/N)"
    if ($choice -notin @("y", "Y")) {
        exit 1
    }
}

if (-not (Get-Command pnpm -ErrorAction SilentlyContinue)) {
    throw "pnpm is not installed. Install it with: npm install -g pnpm"
}

if (-not (Test-Path ".\node_modules")) {
    Write-Host "Installing dependencies with pnpm..."
    pnpm install
}

if (-not (Test-Path ".\.env") -and (Test-Path ".\.env.example")) {
    Copy-Item ".\.env.example" ".\.env"
    Write-Host "Created .env from .env.example"
}

Write-Host "Starting Electron app..."
pnpm start
