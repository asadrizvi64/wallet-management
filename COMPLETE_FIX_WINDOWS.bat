@echo off
REM ============================================================================
REM COMPLETE FIX FOR LOGIN - Windows Version
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - COMPLETE LOGIN FIX
echo =========================================
echo.
echo This script will:
echo 1. Fix the database is_active issue
echo 2. Restart Docker containers
echo 3. Test login
echo.
pause

REM Step 1: Fix database
echo.
echo Step 1: Fixing database...
echo =========================================
echo.

REM Try Docker first
docker exec -i wallet-mysql mysql -u root -proot wallet_db < COMPLETE_LOGIN_FIX.sql
if %ERRORLEVEL% EQU 0 (
    echo [OK] Database fix applied via Docker
    goto :restart
)

REM Try MariaDB
where mariadb >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mariadb -h localhost -u root -proot < COMPLETE_LOGIN_FIX.sql
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Database fix applied via MariaDB
        goto :restart
    )
)

REM Try MySQL
where mysql >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mysql -h localhost -u root -proot < COMPLETE_LOGIN_FIX.sql
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Database fix applied via MySQL
        goto :restart
    )
)

echo [ERROR] Could not apply database fix
echo Please run manually:
echo   docker exec -i wallet-mysql mysql -u root -proot wallet_db < COMPLETE_LOGIN_FIX.sql
echo.
pause
exit /b 1

:restart
REM Step 2: Restart containers
echo.
echo Step 2: Restarting Docker containers...
echo =========================================
echo.

docker-compose down
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to stop containers
    pause
    exit /b 1
)

timeout /t 3 /nobreak >nul

docker-compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to start containers
    pause
    exit /b 1
)

echo [OK] Containers restarted
echo.
echo Waiting for backend to start...
timeout /t 15 /nobreak

REM Step 3: Test login
echo.
echo Step 3: Testing login...
echo =========================================
echo.

curl -s -X POST http://localhost:8080/api/v1/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"asad@example.com\", \"password\": \"password123\"}"

echo.
echo.
echo =========================================
echo FIX COMPLETE
echo =========================================
echo.
echo If you see a token in the response above, login is working!
echo.
echo Test credentials:
echo   Email: asad@example.com
echo   Password: password123
echo.
echo If login still fails, run:
echo   .\DEBUG_LOGIN_WINDOWS.bat
echo.
pause
