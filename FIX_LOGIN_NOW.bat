@echo off
REM ============================================================================
REM LOGIN FIX SCRIPT - Run this to fix login issues immediately (Windows)
REM ============================================================================

echo =========================================
echo WALLET MANAGEMENT - LOGIN FIX SCRIPT
echo =========================================
echo.
echo This script will fix the login authentication issue
echo.

REM Check if MySQL is in PATH
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: MySQL client not found!
    echo Please make sure MySQL is installed and in your PATH
    echo Or run the SQL script manually in MySQL Workbench
    echo.
    pause
    exit /b 1
)

echo Applying database fix...
echo.

REM Run the fix script
mysql -h localhost -u root -proot < COMPLETE_LOGIN_FIX.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Database fix applied successfully!
    echo.
    echo =========================================
    echo NEXT STEPS:
    echo =========================================
    echo 1. Restart your backend application
    echo 2. Try logging in with any registered email and password
    echo 3. If issues persist, check the backend logs
    echo.
    echo Sample test login (from schema.sql):
    echo   Email: asad@example.com
    echo   Password: password123
    echo.
) else (
    echo.
    echo ERROR: Failed to apply fix
    echo.
    echo Manual fix steps:
    echo 1. Open MySQL Workbench or command line
    echo 2. Connect to localhost with user root
    echo 3. Run: source COMPLETE_LOGIN_FIX.sql
    echo 4. Check the output for any errors
    echo.
)

pause
