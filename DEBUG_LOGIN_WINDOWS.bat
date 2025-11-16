@echo off
REM ============================================================================
REM DEBUG LOGIN SCRIPT - Windows Version
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - LOGIN DEBUG
echo =========================================
echo.

REM Check if MariaDB client is available
where mariadb >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Using MariaDB client...
    mariadb -h localhost -u root -proot < DEBUG_LOGIN.sql
    goto :done
)

REM Check if MySQL client is available
where mysql >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Using MySQL client...
    mysql -h localhost -u root -proot < DEBUG_LOGIN.sql
    goto :done
)

REM Neither found, check Docker
echo MySQL/MariaDB client not found in PATH.
echo Trying with Docker...
echo.

docker exec -i wallet-mysql mysql -u root -proot wallet_db < DEBUG_LOGIN.sql
if %ERRORLEVEL% EQU 0 (
    goto :done
)

echo.
echo ERROR: Could not connect to database!
echo.
echo Please try one of these options:
echo.
echo Option 1: Using Docker
echo   docker exec -i wallet-mysql mysql -u root -proot wallet_db < DEBUG_LOGIN.sql
echo.
echo Option 2: Using MySQL Workbench
echo   1. Open MySQL Workbench
echo   2. Connect to localhost with user 'root' password 'root'
echo   3. File -^> Run SQL Script
echo   4. Select DEBUG_LOGIN.sql
echo.
pause
exit /b 1

:done
echo.
echo =========================================
echo Debug information displayed above
echo =========================================
echo.
pause
