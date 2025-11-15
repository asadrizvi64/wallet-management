@echo off
setlocal enabledelayedexpansion

REM ###############################################################################
REM Wallet Management System - Stop Development Servers
REM This script stops the backend and frontend development servers
REM ###############################################################################

echo ==========================================
echo Stopping Development Servers
echo ==========================================
echo.

echo Stopping processes...
echo.

REM Kill Spring Boot processes
echo Looking for Spring Boot processes...
taskkill /F /FI "WINDOWTITLE eq Wallet-Backend*" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Backend window closed
) else (
    echo [INFO] Backend window not found by title
)

REM Kill React dev server
echo Looking for React dev server processes...
taskkill /F /FI "WINDOWTITLE eq Wallet-Frontend*" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Frontend window closed
) else (
    echo [INFO] Frontend window not found by title
)

REM Fallback: Kill by process name
echo.
echo Cleaning up any remaining processes...
echo.

REM Kill any Java processes running Maven Spring Boot
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr /C:"PID:"') do (
    tasklist /FI "PID eq %%a" /V | findstr /I "spring-boot" >nul 2>&1
    if !errorlevel! equ 0 (
        taskkill /F /PID %%a >nul 2>&1
        echo [OK] Stopped Spring Boot process (PID: %%a^)
    )
)

REM Kill Node.js processes (may include React dev server)
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq node.exe" /FO LIST ^| findstr /C:"PID:"') do (
    tasklist /FI "PID eq %%a" /V | findstr /I "react-scripts" >nul 2>&1
    if !errorlevel! equ 0 (
        taskkill /F /PID %%a >nul 2>&1
        echo [OK] Stopped React dev server process (PID: %%a^)
    )
)

REM Alternative: Kill all node and java processes related to development
REM WARNING: This might kill other unrelated processes
REM Uncomment the following lines if you want more aggressive cleanup
REM taskkill /F /IM "java.exe" /FI "WINDOWTITLE eq *maven*" >nul 2>&1
REM taskkill /F /IM "node.exe" /FI "WINDOWTITLE eq *react*" >nul 2>&1

echo.
echo ==========================================
echo Development servers stopped!
echo ==========================================
echo.
echo To start again, run: dev-start.bat
echo.

pause
