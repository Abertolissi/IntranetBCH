@echo off
REM ============================================================
REM QUICK START - INTRANET BANCARIA (Windows)
REM Script de setup rápido para desarrolladores
REM ============================================================

setlocal enabledelayedexpansion

echo ==================================================
echo 🚀 INTRANET BANCARIA - QUICK START (Windows)
echo ==================================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java no encontrado. Instala Java 17+
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do echo ✅ %%i

REM Verificar Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven no encontrado. Instala Maven 3.8+
    pause
    exit /b 1
)
echo ✅ Maven encontrado

REM Verificar Node
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js no encontrado. Instala Node 18+
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('node --version') do echo ✅ Node.js %%i

echo.
echo.
echo *** Configurando Backend ***
cd backend
echo Limpiando build anterior...
call mvn clean > nul 2>&1
echo Descargando dependencias...
call mvn install > nul 2>&1
if errorlevel 1 (
    echo ❌ Error instalando Maven dependencies
    pause
    exit /b 1
)
echo ✅ Backend configurado
cd ..

echo.
echo *** Configurando Frontend ***
cd frontend
if exist node_modules (
    echo Modulos encontrados, skipping npm install
) else (
    echo Instalando dependencias npm...
    call npm install
    if errorlevel 1 (
        echo ❌ Error instalando npm dependencies
        pause
        exit /b 1
    )
)
echo ✅ Frontend configurado
cd ..

echo.
echo *** Base de Datos ***
echo.
echo IMPORTANTE:
echo 1. SQL Server debe estar ejecutándose en localhost:1433
echo 2. Usuario: sa
echo 3. Ejecutar script: database\scripts\01_create_database.sql
echo.
set /p db_ready="¿Has ejecutado el script SQL? (s/n): "
if /i not "%db_ready%"=="s" (
    echo ❌ Script SQL no ejecutado. Hazlo en SSMS y vuelve.
    pause
    exit /b 1
)
echo ✅ Base de datos configurada

echo.
echo *** Iniciando Servicios ***
echo.
echo Se abrirán 2 ventanas Cmd:
echo 1. Backend (Spring Boot en puerto 8080)
echo 2. Frontend (Angular Development Server en puerto 4200)
echo.
pause

REM Backend
echo Iniciando Backend...
start "Backend - Spring Boot" cmd /k "cd backend && mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'"
timeout /t 3 /nobreak

REM Frontend
echo Iniciando Frontend...
start "Frontend - Angular" cmd /k "cd frontend && npm start"

echo.
echo ==================================================
echo ✅ Servicios iniciados!
echo ==================================================
echo.
echo Backend: http://localhost:8080 (API)
echo Frontend: http://localhost:4200
echo.
echo Credenciales:
echo Usuario: admin@banco.local
echo Contraseña: AdminPassword123!
echo.
echo Abriendo navegador en 5 segundos...
echo ==================================================
echo.

timeout /t 5 /nobreak
start http://localhost:4200

endlocal
