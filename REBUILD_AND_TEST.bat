@echo off
REM ============================================================================
REM REBUILD AND TEST - Complete rebuild with authentication fix
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - REBUILD AND TEST
echo =========================================
echo.
echo This script will:
echo 1. Pull latest fixes from Git
echo 2. Stop current containers
echo 3. Rebuild with authentication fix
echo 4. Start containers
echo 5. Test login
echo 6. Show debug logs
echo.
pause

REM Step 1: Pull latest changes
echo.
echo Step 1: Pulling latest fixes...
echo =========================================
echo.
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Git pull failed
    pause
    exit /b 1
)

echo [OK] Latest code pulled
echo.

REM Step 2: Stop containers
echo Step 2: Stopping containers...
echo =========================================
echo.
docker-compose down
timeout /t 3 /nobreak >nul

REM Step 3: Rebuild
echo Step 3: Rebuilding with fixes...
echo =========================================
echo.
echo This may take a few minutes...
echo.
docker-compose build --no-cache
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)

echo [OK] Build successful
echo.

REM Step 4: Start containers
echo Step 4: Starting containers...
echo =========================================
echo.
docker-compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to start containers
    pause
    exit /b 1
)

echo [OK] Containers started
echo.
echo Waiting for backend to start (30 seconds)...
timeout /t 30 /nobreak

REM Step 5: Test login
echo.
echo Step 5: Testing login...
echo =========================================
echo.

REM Test with new user
echo Creating and testing new user...
echo.

set TEST_EMAIL=diagtest_%RANDOM%@example.com
set TEST_USERNAME=diagtest_%RANDOM%

echo Registering user: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"%TEST_USERNAME%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\", \"fullName\": \"Diagnostic Test\", \"phoneNumber\": \"+92-300-8888888\", \"cnicNumber\": \"12345-9999999-9\"}"

echo.
echo.
echo Waiting 2 seconds...
timeout /t 2 /nobreak >nul

echo Testing login with: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\"}"

echo.
echo.

REM Step 6: Show debug logs
echo.
echo Step 6: Backend Debug Logs
echo =========================================
echo.
echo Last 50 lines with DEBUG markers:
echo.

docker-compose logs --tail=50 backend | findstr /i "DEBUG login authentication"

echo.
echo.
echo =========================================
echo FULL LOGS ANALYSIS
echo =========================================
echo.
echo To see ALL recent logs run:
echo   docker-compose logs --tail=100 backend
echo.
echo To see continuous logs run:
echo   docker-compose logs -f backend
echo.
echo To see only errors run:
echo   docker-compose logs backend 2^>^&1 ^| findstr /i "error exception failed"
echo.
pause
