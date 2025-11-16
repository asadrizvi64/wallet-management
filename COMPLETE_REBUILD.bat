@echo off
REM ============================================================================
REM COMPLETE REBUILD - Properly handles existing containers
REM ============================================================================

echo =========================================
echo COMPLETE REBUILD - Clean Start
echo =========================================
echo.

REM Pull latest
echo Pulling latest code...
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
echo.

REM Stop and remove ALL containers by name (avoid docker-compose conflicts)
echo Removing existing containers...
docker stop wallet-backend wallet-mysql wallet-frontend 2>nul
docker rm wallet-backend wallet-mysql wallet-frontend 2>nul
echo.

REM Remove backend images
echo Removing backend images...
docker rmi wallet-management-backend wallet-management_backend 2>nul
echo.

REM Remove network if exists
echo Cleaning network...
docker network rm wallet-management_wallet-network 2>nul
echo.

REM Build backend only (skip frontend)
echo Building backend from scratch...
cd backend
docker build --no-cache -t wallet-backend .
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Backend build failed!
    cd ..
    pause
    exit /b 1
)
cd ..
echo [OK] Backend built successfully
echo.

REM Start MySQL first
echo Starting MySQL...
docker run -d ^
  --name wallet-mysql ^
  --network wallet-management_wallet-network ^
  -e MYSQL_ROOT_PASSWORD=root ^
  -e MYSQL_DATABASE=wallet_db ^
  -p 3306:3306 ^
  -v wallet-management_mysql-data:/var/lib/mysql ^
  mysql:8.0

REM Create network if needed
docker network create wallet-management_wallet-network 2>nul

REM Start MySQL with network
docker stop wallet-mysql 2>nul
docker rm wallet-mysql 2>nul
docker run -d ^
  --name wallet-mysql ^
  --network wallet-management_wallet-network ^
  -e MYSQL_ROOT_PASSWORD=root ^
  -e MYSQL_DATABASE=wallet_db ^
  -p 3306:3306 ^
  mysql:8.0

echo Waiting for MySQL to start (15 seconds)...
timeout /t 15 /nobreak >nul

REM Start backend
echo Starting backend...
docker run -d ^
  --name wallet-backend ^
  --network wallet-management_wallet-network ^
  -e SPRING_DATASOURCE_URL=jdbc:mysql://wallet-mysql:3306/wallet_db ^
  -e SPRING_DATASOURCE_USERNAME=root ^
  -e SPRING_DATASOURCE_PASSWORD=root ^
  -p 8080:8080 ^
  wallet-backend

echo Waiting for backend to start (20 seconds)...
timeout /t 20 /nobreak >nul

REM Check if backend is running
echo.
echo Checking backend status...
curl -s http://localhost:8080/api/v1/users/register >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Backend is running!
) else (
    echo [WARNING] Backend may not be ready
    timeout /t 10 /nobreak >nul
)

echo.
echo =========================================
echo LOGIN TESTS
echo =========================================
echo.

REM Test 1: Register new user
set TEST_EMAIL=complete_%RANDOM%@example.com
set TEST_USER=complete_%RANDOM%

echo Test 1: Registering %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"%TEST_USER%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Complete123!\", \"fullName\": \"Complete Test\", \"phoneNumber\": \"+92-300-4444444\", \"cnicNumber\": \"12345-4444444-4\"}"
echo.
echo.

timeout /t 2 /nobreak >nul

REM Test 2: Login with new user
echo Test 2: Logging in with %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Complete123!\"}"
echo.
echo.

REM Show logs
echo =========================================
echo BACKEND LOGS - Last 50 lines
echo =========================================
docker logs --tail=50 wallet-backend
echo.
echo.

echo =========================================
echo DEBUG OUTPUT
echo =========================================
docker logs wallet-backend 2>&1 | findstr /i "DEBUG"
echo.

echo If you see DEBUG lines above, the new code is running!
echo If login shows "success":true, authentication is FIXED!
echo.
pause
