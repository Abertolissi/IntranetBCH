@echo off
setlocal

cd /d "%~dp0"
echo ================================================
echo INTRANET BANCARIA - ARRANQUE DOCKER
echo ================================================

powershell -NoProfile -ExecutionPolicy Bypass -File run-intranet.ps1

if errorlevel 1 (
  echo.
  echo Falló arranque. Revisar logs con docker compose logs -f
  pause
  endlocal
  exit /b 1
)

echo.
echo Listo. Frontend: http://localhost:4200
echo Backend: http://localhost:8080/api
pause

endlocal