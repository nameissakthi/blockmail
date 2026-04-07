Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Set-Location $PSScriptRoot

function Test-Ganache {
    $body = @{ jsonrpc = "2.0"; method = "eth_blockNumber"; params = @(); id = 1 } | ConvertTo-Json -Compress
    try {
        $null = Invoke-RestMethod -Uri "http://127.0.0.1:7545" -Method Post -ContentType "application/json" -Body $body
        return $true
    } catch {
        return $false
    }
}

function Set-EnvValue {
    param(
        [Parameter(Mandatory = $true)][string]$FilePath,
        [Parameter(Mandatory = $true)][string]$Key,
        [Parameter(Mandatory = $true)][string]$Value
    )

    $lines = Get-Content -Path $FilePath
    $pattern = "^$([regex]::Escape($Key))="
    $updated = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match $pattern) {
            $lines[$i] = "$Key=$Value"
            $updated = $true
            break
        }
    }

    if (-not $updated) {
        $lines += "$Key=$Value"
    }

    Set-Content -Path $FilePath -Value $lines
}

Write-Host "===================================================="
Write-Host "Quantum Mail Blockchain Setup (Windows)"
Write-Host "===================================================="

Write-Host "[1/6] Checking Ganache on http://127.0.0.1:7545 ..."
if (-not (Test-Ganache)) {
    throw "Ganache is not reachable on http://127.0.0.1:7545. Start Ganache and run again."
}
Write-Host "Ganache is running."

Write-Host "[2/6] Installing Node dependencies..."
npm install

Write-Host "[3/6] Compiling smart contracts..."
npx hardhat compile

Write-Host "[4/6] Deploying contract to Ganache..."
npx hardhat run scripts/deploy.js --network ganache

if (-not (Test-Path ".\deployment-info.json")) {
    throw "deployment-info.json was not generated. Deployment appears to have failed."
}

$deploymentInfo = Get-Content ".\deployment-info.json" | ConvertFrom-Json
$contractAddress = [string]$deploymentInfo.contractAddress
if ([string]::IsNullOrWhiteSpace($contractAddress)) {
    throw "Could not read contractAddress from deployment-info.json"
}
Write-Host "Contract deployed at: $contractAddress"

Write-Host "[5/6] Updating .env values..."
if (-not (Test-Path ".\.env")) {
    if (-not (Test-Path ".\.env.example")) {
        throw ".env.example not found. Cannot create .env"
    }
    Copy-Item ".\.env.example" ".\.env"
}

Set-EnvValue -FilePath ".\.env" -Key "BLOCKCHAIN_CONTRACT_ADDRESS" -Value $contractAddress
Set-EnvValue -FilePath ".\.env" -Key "BLOCKCHAIN_ENABLED" -Value "true"
Write-Host ".env updated successfully."

Write-Host "[6/6] Building Java project..."
& ".\mvnw.cmd" "clean" "compile" "-DskipTests"

Write-Host "Setup complete."
Write-Host "Next: run .\start.ps1"
