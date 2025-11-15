@echo off
setlocal enabledelayedexpansion

REM ###############################################################################
REM Wallet Management System - Development Mode Startup
REM This script starts backend and frontend in development mode (without Docker)
REM ###############################################################################

echo ==========================================
echo Wallet Management System - Development Mode
echo ==========================================
echo.

REM Check prerequisites
echo Checking prerequisites...
echo.

REM Check Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed
    echo Please install Java 17 or higher
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
) else (
    echo [OK] Java found
    java -version 2>&1 | findstr /C:"version"
)

REM Check Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed
    echo Please install Maven 3.6 or higher
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
) else (
    echo [OK] Maven found
    mvn -version 2>&1 | findstr /C:"Apache Maven"
)

REM Check Node.js
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed
    echo Please install Node.js 16 or higher
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
) else (
    echo [OK] Node.js found
    node -v
)

REM Check npm
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] npm is not installed
    echo Please install npm
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
) else (
    echo [OK] npm found
    npm -v
)

REM Check MySQL
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] MySQL client not found in PATH
    echo Make sure MySQL server is running on localhost:3306
) else (
    echo [OK] MySQL client found
)

echo.
echo ==========================================
echo Prerequisites check completed!
echo ==========================================
echo.

REM Check and install frontend dependencies if needed
if not exist "frontend\node_modules" (
    echo Frontend dependencies not found.
    echo Installing npm packages...
    cd frontend
    call npm install
    cd ..
    echo.
)

echo ==========================================
echo Starting services...
echo ==========================================
echo.

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

echo Starting MySQL (if using Docker):
echo If you want to use MySQL in Docker, run:
echo docker run -d --name wallet-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wallet_db -p 3306:3306 mysql:8.0
echo.
echo Press any key to continue when MySQL is ready...
pause >nul

echo.
echo 1. Starting Backend (Spring Boot)...
cd backend
start "Wallet-Backend" cmd /c "mvn spring-boot:run > ..\logs\backend.log 2>&1"
cd ..
echo    Backend started in new window
echo    Logs: logs\backend.log

echo.
echo 2. Starting Frontend (React)...
cd frontend
start "Wallet-Frontend" cmd /c "npm start > ..\logs\frontend.log 2>&1"
cd ..
echo    Frontend started in new window
echo    Logs: logs\frontend.log

echo.
echo ==========================================
echo Development servers started!
echo ==========================================
echo.
echo Access your application:
echo ==========================================
echo Frontend (React):     http://localhost:3000
echo Backend API:          http://localhost:8080
echo Swagger UI:           http://localhost:8080/swagger-ui.html
echo.
echo To stop the servers:
echo ==========================================
echo Run: dev-stop.bat
echo.
echo View logs:
echo ==========================================
echo Backend:  type logs\backend.log
echo Frontend: type logs\frontend.log
echo.
echo Or use: tail -f logs\backend.log (if you have Git Bash/WSL)
echo.

echo Waiting for services to start...
echo This may take 30-60 seconds...
echo.
timeout /t 5 /nobreak >nul

echo ==========================================
echo Development environment is ready!
echo ==========================================
echo.
echo Press any key to return to prompt (servers will continue running)...
pause >nul
