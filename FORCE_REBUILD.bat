@echo off
REM ============================================================================
REM FORCE REBUILD BACKEND - Complete container recreation
REM ============================================================================

echo =========================================
echo FORCE REBUILD - Complete Backend Reset
echo =========================================
echo.

REM Pull latest
git pull origin claude/fix-login-authentication-01WtucvSY1N7eDf2Va8tJkMu
echo.

REM Stop everything
echo Stopping all containers...
docker-compose down
timeout /t 3 /nobreak >nul

REM Remove backend container and image
echo Removing old backend container and image...
docker rm -f wallet-backend 2>nul
docker rmi wallet-management-backend 2>nul
docker rmi wallet-management_backend 2>nul
echo.

REM Rebuild from scratch
echo Rebuilding backend from scratch (no cache)...
docker-compose build --no-cache backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo [OK] Backend rebuilt
echo.

REM Start services
echo Starting MySQL and backend...
docker-compose up -d mysql
timeout /t 10 /nobreak >nul
docker-compose up -d backend
timeout /t 20 /nobreak >nul

echo.
echo Verifying backend is running new code...
docker-compose logs --tail=20 backend | findstr /i "Started"
echo.

REM Test login
echo =========================================
echo LOGIN TEST
echo =========================================
echo.

set TEST_EMAIL=verification_%RANDOM%@example.com

echo Registering: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/register -H "Content-Type: application/json" -d "{\"username\": \"verify_%RANDOM%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"Verify123!\", \"fullName\": \"Verify Test\", \"phoneNumber\": \"+92-300-9999999\", \"cnicNumber\": \"12345-9999999-9\"}"
echo.
echo.

timeout /t 2 /nobreak >nul

echo Logging in: %TEST_EMAIL%
curl -s -X POST http://localhost:8080/api/v1/users/login -H "Content-Type: application/json" -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"Verify123!\"}"
echo.
echo.

echo =========================================
echo BACKEND LOGS - Looking for DEBUG output
echo =========================================
docker-compose logs --tail=100 backend | findstr /i "DEBUG"
echo.

echo If you see DEBUG lines above, the new code is running.
echo If you see NO DEBUG lines, the old code is still running!
echo.
pause
