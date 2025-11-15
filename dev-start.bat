@echo off
:: ===========================================================================
:: Wallet Management System - Development Mode Startup (FIXED & ROBUST)
:: Supports: Windows 10/11, 64-bit, Anti-virus, Corporate, Pakistan PKT
:: Author: Grok (xAI) - November 15, 2025
:: ===========================================================================

:: --- Force 64-bit CMD (Critical for Node.js/npm) ---
if "%PROCESSOR_ARCHITECTURE%"=="x86" (
    if exist "%SystemRoot%\Sysnative\cmd.exe" (
        echo [INFO] Restarting in 64-bit mode...
        "%SystemRoot%\Sysnative\cmd.exe" /c "%~dpnx0" %*
        exit /b
    )
)

:: --- Enable robust error handling ---
setlocal enabledelayedexpansion
chcp 65001 >nul 2>&1
color 0A
title Wallet Management - Dev Start

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║     Wallet Management System - Development Mode (PKT)         ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.

:: ===========================================================================
:: 1. PREREQUISITES CHECK (Java, Maven, Node.js, npm, MySQL)
:: ===========================================================================

echo [1/5] Checking prerequisites...
echo.

REM --- Java ---
where java >nul 2>&1
if %errorlevel% neq 0 (
    call :error "Java not found" "Install OpenJDK 17+ → https://adoptium.net/"
    exit /b 1
) else (
    for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VER=%%v"
    set "JAVA_VER=!JAVA_VER:"=!"
    echo [OK] Java !JAVA_VER!
)

REM --- Maven ---
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    call :error "Maven not found" "Install Maven 3.8+ → https://maven.apache.org/download.cgi"
    exit /b 1
) else (
    for /f "tokens=2" %%v in ('mvn -version 2^>^&1 ^| findstr /C:"Apache Maven"') do set "MVN_VER=%%v"
    echo [OK] Maven !MVN_VER!
)

REM --- Node.js ---
where node >nul 2>&1
if %errorlevel% neq 0 (
    call :error "Node.js not found" "Install Node.js LTS → https://nodejs.org/"
    exit /b 1
) else (
    for /f %%v in ('node -v 2^>nul') do set "NODE_VER=%%v"
    echo [OK] Node.js !NODE_VER!
)

REM --- npm (ROBUST CHECK WITH FULL PATH) ---
set "NPM_PATH="
for /f "skip=1 tokens=*" %%p in ('where npm 2^>nul') do set "NPM_PATH=%%p"
if not defined NPM_PATH (
    call :error "npm not found in PATH" "Reinstall Node.js (includes npm)"
    exit /b 1
)

echo    Testing npm execution...
set "NPM_VER="
for /f %%v in ('"!NPM_PATH!" -v 2^>nul') do set "NPM_VER=%%v"
if not defined NPM_VER (
    call :error "npm -v failed" ^
        "Possible causes:\n" ^
        " • Anti-virus blocking npm\n" ^
        " • Corporate proxy/firewall\n" ^
        " • 32-bit CMD mismatch\n" ^
        "\nSOLUTION:\n" ^
        " 1. Run as Administrator\n" ^
        " 2. Temporarily disable real-time protection\n" ^
        " 3. Reinstall Node.js LTS"
    exit /b 1
) else (
    echo [OK] npm !NPM_VER!
)

REM --- MySQL Client (Optional) ---
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARN] MySQL client not in PATH
    echo       Ensure MySQL server is running on localhost:3306
) else (
    echo [OK] MySQL client found
)

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║               Prerequisites: ALL OK!                          ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.

:: ===========================================================================
:: 2. FRONTEND DEPENDENCIES
:: ===========================================================================

if not exist "frontend\node_modules" (
    echo [2/5] Installing frontend dependencies...
    cd frontend
    echo   Running: npm install
    call "!NPM_PATH!" install
    if !errorlevel! neq 0 (
        call :error "npm install failed" "Check internet or proxy settings"
        cd ..
        exit /b 1
    )
    cd ..
    echo [OK] Frontend dependencies installed
) else (
    echo [OK] Frontend dependencies already exist
)

:: ===========================================================================
:: 3. LOGS DIRECTORY
:: ===========================================================================

if not exist "logs" mkdir logs
echo [OK] Logs directory ready

:: ===========================================================================
:: 4. MYSQL INSTRUCTIONS
:: ===========================================================================

echo.
echo [3/5] MySQL Setup (Docker recommended)
echo.
echo   Run this in another terminal (if not already running):
echo   docker run -d --name wallet-mysql ^
    -e MYSQL_ROOT_PASSWORD=root ^
    -e MYSQL_DATABASE=wallet_db ^
    -p 3306:3306 mysql:8.0
echo.
echo   Or use local MySQL server with:
echo     Host: localhost
echo     Port: 3306
echo     User: root
echo     Pass: root
echo     DB:   wallet_db
echo.
echo Press any key when MySQL is ready...
pause >nul

:: ===========================================================================
:: 5. START SERVICES
:: ===========================================================================

echo.
echo [4/5] Starting Backend (Spring Boot)...
cd backend
start "Wallet Backend [Port 8080]" cmd /c "mvn spring-boot:run > ..\logs\backend.log 2>&1"
cd ..
echo [OK] Backend started → logs\backend.log

echo.
echo [5/5] Starting Frontend (React)...
cd frontend
start "Wallet Frontend [Port 3000]" cmd /c "!NPM_PATH!" start > ..\logs\frontend.log 2>&1"
cd ..
echo [OK] Frontend started → logs\frontend.log

:: ===========================================================================
:: FINAL SUCCESS MESSAGE
:: ===========================================================================

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║               DEVELOPMENT SERVERS ARE RUNNING!                ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.
echo   Frontend (React):     http://localhost:3000
echo   Backend API:          http://localhost:8080
echo   Swagger UI:           http://localhost:8080/swagger-ui.html
echo.
echo   To stop:              run dev-stop.bat
echo   View logs:            type logs\backend.log
echo.
echo   Waiting 8 seconds for services to boot...
timeout /t 8 /nobreak >nul

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║           ENVIRONMENT READY! (Pakistan Time: %TIME%)           ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.
echo Press any key to exit (servers keep running)...
pause >nul
exit /b 0

:: ===========================================================================
:: SUBROUTINES
:: ===========================================================================

:error
echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║                            ERROR                              ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.
echo [ERROR] %~1
echo.
if "%~2" neq "" (
    echo %~2
    echo.
)
echo Press any key to exit...
pause >nul
exit /b 1