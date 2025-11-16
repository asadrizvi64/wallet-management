@echo off
REM ============================================================================
REM LOGIN TEST SCRIPT - Windows Version
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - LOGIN TEST
echo =========================================
echo.

REM Check if curl is available
where curl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: curl not found!
    echo Please install curl or test manually
    pause
    exit /b 1
)

REM Test 1: Check if backend is running
echo Test 1: Checking if backend is running on port 8080...
curl -s http://localhost:8080/api/v1/users/register >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Backend is running
) else (
    echo [ERROR] Backend is NOT running or not accessible
    echo.
    echo Start the backend first:
    echo   docker-compose up -d
    echo   OR
    echo   .\dev-start.bat
    echo.
    pause
    exit /b 1
)

echo.

REM Test 2: Try login with sample credentials
echo Test 2: Testing login with sample credentials...
echo Email: asad@example.com
echo Password: password123
echo.

echo Response:
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"asad@example.com\", \"password\": \"password123\"}"

echo.
echo.

REM Test 3: Try registering a new user
echo Test 3: Testing registration with new user...
set TEST_EMAIL=test_%RANDOM%@example.com
set TEST_USERNAME=testuser_%RANDOM%

echo.
echo Registration Response:
curl -s -X POST http://localhost:8080/api/v1/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"%TEST_USERNAME%\", \"email\": \"%TEST_EMAIL%\", \"password\": \"testpass123\", \"fullName\": \"Test User\", \"phoneNumber\": \"+92-300-9999999\", \"cnicNumber\": \"12345-1234567-1\"}"

echo.
echo.

REM Test 4: Try login with new user
echo Test 4: Testing login with newly registered user...
timeout /t 2 /nobreak >nul

echo.
echo Login Response:
curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%TEST_EMAIL%\", \"password\": \"testpass123\"}"

echo.
echo.
echo =========================================
echo DEBUGGING STEPS:
echo =========================================
echo 1. Check backend logs:
echo    docker-compose logs backend
echo.
echo 2. Check database passwords:
echo    .\DEBUG_LOGIN_WINDOWS.bat
echo.
echo 3. Restart backend:
echo    docker-compose down
echo    docker-compose up -d
echo.
echo 4. Try sample login:
echo    Email: asad@example.com
echo    Password: password123
echo.
pause
