@echo off
REM ============================================================================
REM REBUILD BACKEND ONLY - Faster rebuild focusing on authentication fix
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - REBUILD BACKEND ONLY
echo =========================================
echo.
echo This script will:
echo 1. Pull latest authentication fixes
echo 2. Stop backend container
echo 3. Rebuild backend with authentication fix
echo 4. Start backend
echo 5. Test login
echo.
pause

REM Step 1: Pull latest changes
echo.
echo Step 1: Pulling latest fixes...
echo =========================================
echo.
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Git pull had issues, continuing anyway...
)

echo.

REM Step 2: Stop backend
echo Step 2: Stopping backend container...
echo =========================================
echo.
docker-compose stop backend
timeout /t 2 /nobreak >nul

REM Step 3: Rebuild backend only
echo Step 3: Rebuilding backend with authentication fix...
echo =========================================
echo.
echo This may take a few minutes...
echo.
docker-compose build --no-cache backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Backend build failed
    echo.
    echo Try running manually:
    echo   cd backend
    echo   docker build --no-cache -t wallet-backend .
    echo.
    pause
    exit /b 1
)

echo [OK] Backend build successful
echo.

REM Step 4: Start backend
echo Step 4: Starting backend...
echo =========================================
echo.
docker-compose up -d backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to start backend
    pause
    exit /b 1
)

echo [OK] Backend started
echo.
echo Waiting for backend to initialize (20 seconds)...
timeout /t 20 /nobreak

REM Step 5: Test login
echo.
echo Step 5: Testing login...
echo =========================================
echo.

echo Test A: Sample user (asad@example.com)
echo.
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"asad@example.com\", \"password\": \"password123\"}"

echo.
echo.

echo Test B: Creating new test user...
echo.
set TEST_EMAIL=quicktest_%RANDOM%@example.com
set TEST_USERNAME=quicktest_%RANDOM%

curl -s -X POST http://localhost:8080/api/v1/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"%TEST_USERNAME%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\", \"fullName\": \"Quick Test\", \"phoneNumber\": \"+92-300-7777777\", \"cnicNumber\": \"12345-8888888-8\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test C: Login with new user (%TEST_EMAIL%)
echo.
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Test123!\"}"

echo.
echo.

REM Step 6: Show debug logs
echo.
echo Step 6: Checking backend logs for DEBUG output...
echo =========================================
echo.

docker-compose logs --tail=30 backend | findstr /i "DEBUG"

echo.
echo.
echo =========================================
echo DONE
echo =========================================
echo.
echo If you see "success":true and a "token" above, LOGIN IS WORKING!
echo.
echo If login still fails, check full logs:
echo   docker-compose logs --tail=100 backend
echo.
echo Or run diagnostic:
echo   .\GET_BACKEND_LOGS.bat
echo.
pause
