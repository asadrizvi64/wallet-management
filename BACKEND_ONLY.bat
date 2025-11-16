@echo off
REM ============================================================================
REM BACKEND ONLY - Skip frontend, rebuild and test backend authentication
REM ============================================================================

echo =========================================
echo BACKEND ONLY REBUILD - Skip Frontend
echo =========================================
echo.

REM Pull latest
echo Pulling latest code...
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
echo.

REM Stop and remove backend
echo Stopping backend...
docker stop wallet-backend 2>nul
docker rm wallet-backend 2>nul
timeout /t 2 /nobreak >nul

REM Build backend only
echo.
echo Building backend with authentication fix...
echo This may take a few minutes...
echo.
docker-compose build --no-cache backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Backend build failed!
    pause
    exit /b 1
)

echo [OK] Backend built successfully!
echo.

REM Start only MySQL and backend (skip frontend)
echo Starting MySQL and backend only...
docker-compose up -d mysql backend
echo.
echo Waiting for services to start (30 seconds)...
timeout /t 30 /nobreak

REM Test backend is running
echo.
echo Checking backend status...
curl -s http://localhost:8080/api/v1/users/register >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Backend is running
) else (
    echo [WARNING] Backend may not be ready yet, trying anyway...
)

echo.
echo =========================================
echo LOGIN TESTS
echo =========================================
echo.

REM Test 1: Sample user
echo Test 1: Sample user (asad@example.com / password123)
echo.
curl -s -X POST http://localhost:8080/api/v1/users/login -H "Content-Type: application/json" -d "{\"email\": \"asad@example.com\", \"password\": \"password123\"}"
echo.
echo.

REM Test 2: New user
echo Test 2: Register and login new user
echo.
set TEST_EMAIL=finaltest_%RANDOM%@example.com
set TEST_USER=finaltest_%RANDOM%

echo Registering: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/register -H "Content-Type: application/json" -d "{\"username\": \"%TEST_USER%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Final123!\", \"fullName\": \"Final Test\", \"phoneNumber\": \"+92-300-5555555\", \"cnicNumber\": \"12345-5555555-5\"}"
echo.
echo.

timeout /t 2 /nobreak >nul

echo Logging in: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/login -H "Content-Type: application/json" -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Final123!\"}"
echo.
echo.

REM Show debug logs
echo =========================================
echo DEBUG LOGS (Last 40 lines with DEBUG)
echo =========================================
docker-compose logs --tail=100 backend | findstr /i "DEBUG"
echo.
echo.

echo =========================================
echo RESULTS
echo =========================================
echo.
echo Look at the JSON responses above:
echo.
echo SUCCESS: You should see "success":true and a "token"
echo FAILURE: You will see "success":false and "Invalid email or password"
echo.
echo If login STILL fails, let's check the full logs:
echo   docker-compose logs --tail=200 backend
echo.
pause
