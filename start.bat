@echo off
setlocal

REM ###############################################################################
REM Wallet Management System - Startup Script
REM This script starts the entire application using Docker Compose
REM ###############################################################################

echo ==================================
echo Wallet Management System - Startup
echo ==================================
echo.

REM Check if Docker is installed
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not installed
    echo Please install Docker from https://docs.docker.com/get-docker/
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)

REM Check if Docker Compose is available
where docker-compose >nul 2>&1
if %errorlevel% equ 0 (
    set COMPOSE_CMD=docker-compose
    goto :compose_found
)

docker compose version >nul 2>&1
if %errorlevel% equ 0 (
    set COMPOSE_CMD=docker compose
    goto :compose_found
)

echo [ERROR] Docker Compose is not installed
echo Please install Docker Compose from https://docs.docker.com/compose/install/
echo.
echo Press any key to exit...
pause >nul
exit /b 1

:compose_found
echo Starting services...
echo.

REM Start Docker Compose services
%COMPOSE_CMD% up -d

REM Check if services started successfully
if %errorlevel% equ 0 (
    echo.
    echo [OK] Services started successfully!
    echo.
    echo ==================================
    echo Access your application at:
    echo ==================================
    echo Frontend ^(React^):     http://localhost:3000
    echo Backend API:          http://localhost:8080
    echo Swagger UI:           http://localhost:8080/swagger-ui.html
    echo MySQL Database:       localhost:3306
    echo.
    echo ==================================
    echo Default Test Accounts:
    echo ==================================
    echo Username: asad_khan
    echo Password: password123
    echo.
    echo Username: ali_ahmed
    echo Password: password123
    echo.
    echo ==================================
    echo Useful Commands:
    echo ==================================
    echo Stop services:        stop.bat
    echo View logs:            %COMPOSE_CMD% logs -f
    echo Check status:         %COMPOSE_CMD% ps
    echo Restart services:     %COMPOSE_CMD% restart
    echo.
) else (
    echo [ERROR] Failed to start services
    echo Please check the error messages above
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)

pause
