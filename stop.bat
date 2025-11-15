@echo off
setlocal

REM ###############################################################################
REM Wallet Management System - Stop Script
REM This script stops all running Docker containers
REM ###############################################################################

echo ==================================
echo Wallet Management System - Shutdown
echo ==================================
echo.

echo Stopping services...
echo.

REM Check which Docker Compose command is available
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
exit /b 1

:compose_found
REM Stop Docker Compose services
%COMPOSE_CMD% down

REM Check if services stopped successfully
if %errorlevel% equ 0 (
    echo.
    echo [OK] All services stopped successfully!
    echo.
    echo ==================================
    echo To start again, run: start.bat
    echo.
    echo To remove all data ^(including database^):
    echo %COMPOSE_CMD% down -v
    echo ==================================
    echo.
) else (
    echo [ERROR] Failed to stop services
    echo Please check the error messages above
    exit /b 1
)

pause
