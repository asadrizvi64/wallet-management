@echo off
REM Get backend logs for login debugging

echo ========================================
echo BACKEND LOGS - Last 200 lines
echo ========================================
echo.

docker-compose logs --tail=200 backend > backend_logs.txt

echo Logs saved to backend_logs.txt
echo.
echo Looking for authentication errors...
echo.

findstr /i /c:"authentication" /c:"login" /c:"error" /c:"exception" /c:"password" /c:"BadCredentials" /c:"DisabledException" backend_logs.txt

echo.
echo ========================================
echo Full logs saved in: backend_logs.txt
echo ========================================
echo.
pause
