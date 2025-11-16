# Test Login - PowerShell Version (No curl needed)

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "LOGIN TEST - PowerShell Edition" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Check if backend is running
Write-Host "Checking backend status..." -ForegroundColor Yellow
docker ps | Select-String "wallet-backend"
Write-Host ""

# Wait for backend to be ready
Write-Host "Waiting for backend to start (25 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 25

# Test 1: Register a new user
Write-Host "Test 1: Registering new user..." -ForegroundColor Green
Write-Host ""

$registerBody = @{
    username = "pwshtest999"
    email = "pwshtest999@example.com"
    password = "Test999!"
    fullName = "PowerShell Test"
    phoneNumber = "+92-311-8888888"
    cnicNumber = "88888-8888888-8"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody

    Write-Host "Registration Response:" -ForegroundColor Green
    $registerResponse | ConvertTo-Json -Depth 5
    Write-Host ""

    if ($registerResponse.success -eq $true) {
        Write-Host "[OK] Registration successful!" -ForegroundColor Green
    } else {
        Write-Host "[WARNING] Registration failed: $($registerResponse.message)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "[ERROR] Registration failed: $_" -ForegroundColor Red
}

Write-Host ""
Start-Sleep -Seconds 2

# Test 2: Login with the new user
Write-Host "Test 2: Logging in with pwshtest999@example.com..." -ForegroundColor Green
Write-Host ""

$loginBody = @{
    email = "pwshtest999@example.com"
    password = "Test999!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody

    Write-Host "Login Response:" -ForegroundColor Green
    $loginResponse | ConvertTo-Json -Depth 5
    Write-Host ""

    if ($loginResponse.success -eq $true) {
        Write-Host "=========================================" -ForegroundColor Green
        Write-Host "SUCCESS! LOGIN IS WORKING!" -ForegroundColor Green
        Write-Host "=========================================" -ForegroundColor Green
        Write-Host "Token: $($loginResponse.data.token.Substring(0, 50))..." -ForegroundColor Cyan
    } else {
        Write-Host "=========================================" -ForegroundColor Red
        Write-Host "LOGIN FAILED" -ForegroundColor Red
        Write-Host "=========================================" -ForegroundColor Red
        Write-Host "Message: $($loginResponse.message)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "[ERROR] Login request failed: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "DEBUG LOGS" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Check for DEBUG logs
$debugLogs = docker logs wallet-backend 2>&1 | Select-String "DEBUG"
if ($debugLogs) {
    Write-Host "DEBUG logs found (NEW CODE IS RUNNING):" -ForegroundColor Green
    $debugLogs | Select-Object -Last 20
} else {
    Write-Host "WARNING: No DEBUG logs found - OLD CODE may still be running!" -ForegroundColor Red
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "RESULTS SUMMARY" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Look above for:" -ForegroundColor Yellow
Write-Host "  - Registration response (should show success)" -ForegroundColor White
Write-Host "  - Login response (should show success and token)" -ForegroundColor White
Write-Host "  - DEBUG logs (proves new code is running)" -ForegroundColor White
Write-Host ""
