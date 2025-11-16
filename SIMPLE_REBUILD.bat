@echo off
REM ============================================================================
REM SIMPLE BACKEND REBUILD - Works with existing containers
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - SIMPLE BACKEND REBUILD
echo =========================================
echo.

REM Step 1: Pull latest code
echo Step 1: Pulling latest fixes...
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
echo.

REM Step 2: Remove old backend container
echo Step 2: Removing old backend container...
docker rm -f wallet-backend 2>nul
timeout /t 2 /nobreak >nul
echo.

REM Step 3: Rebuild backend
echo Step 3: Rebuilding backend with authentication fix...
docker-compose build --no-cache backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo [OK] Build successful
echo.

REM Step 4: Start all services (MySQL will stay running, backend will be recreated)
echo Step 4: Starting services...
docker-compose up -d
echo.
echo Waiting for backend to start (25 seconds)...
timeout /t 25 /nobreak

REM Step 5: Test login
echo.
echo Step 5: Testing login...
echo =========================================
echo.

echo Test A: Sample user (asad@example.com / password123)
echo.
curl -s -X POST http://localhost:8080/api/v1/users/login -H "Content-Type: application/json" -d "{\"email\": \"asad@example.com\", \"password\": \"password123\"}"
echo.
echo.

echo Test B: New user registration and login
echo.
set TEST_EMAIL=test_%RANDOM%@example.com
curl -s -X POST http://localhost:8080/api/v1/users/register -H "Content-Type: application/json" -d "{\"username\": \"test_%RANDOM%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\", \"fullName\": \"Test User\", \"phoneNumber\": \"+92-300-1111111\", \"cnicNumber\": \"12345-1111111-1\"}"
echo.
echo.
timeout /t 2 /nobreak >nul

echo Logging in with new user...
curl -s -X POST http://localhost:8080/api/v1/users/login -H "Content-Type: application/json" -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\"}"
echo.
echo.

REM Step 6: Check logs
echo Step 6: Backend debug logs...
echo =========================================
docker-compose logs --tail=50 backend | findstr /i "DEBUG"
echo.
echo.
echo If you see "success":true above, LOGIN IS WORKING!
echo.
echo Full logs: docker-compose logs backend
echo.
pause
