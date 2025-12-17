@echo off
REM Script de inicio rápido para Reporting Service (Windows)
REM Autor: Sistema Distribuido

echo ==========================================
echo    REPORTING SERVICE - Quick Start
echo ==========================================
echo.

REM Verificar Java
echo Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado. Por favor instale Java 17 o superior.
    pause
    exit /b 1
)

for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo [OK] Java version: %%i
)
echo.

REM Verificar Maven
echo Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [WARN] Maven no encontrado. Usando Maven Wrapper...
    set MVN_CMD=mvnw.cmd
) else (
    set MVN_CMD=mvn
    echo [OK] Maven encontrado
)
echo.

REM Menu de opciones
:menu
echo Seleccione una opcion:
echo 1) Compilar proyecto
echo 2) Ejecutar tests
echo 3) Empaquetar aplicacion
echo 4) Ejecutar aplicacion
echo 5) Ejecutar con Docker Compose
echo 6) Limpiar y compilar todo
echo 7) Salir
echo.

set /p option="Opcion: "

if "%option%"=="1" goto compile
if "%option%"=="2" goto test
if "%option%"=="3" goto package
if "%option%"=="4" goto run
if "%option%"=="5" goto docker
if "%option%"=="6" goto clean_build
if "%option%"=="7" goto exit
goto invalid

:compile
echo Compilando proyecto...
%MVN_CMD% clean compile
goto end

:test
echo Ejecutando tests...
%MVN_CMD% test
goto end

:package
echo Empaquetando aplicacion...
%MVN_CMD% clean package -DskipTests
echo [OK] JAR generado en: target\reporting-service-0.0.1-SNAPSHOT.jar
goto end

:run
echo Ejecutando aplicacion...
if not exist "target\reporting-service-0.0.1-SNAPSHOT.jar" (
    echo [WARN] JAR no encontrado. Compilando primero...
    %MVN_CMD% clean package -DskipTests
)
echo [OK] Iniciando Reporting Service en puerto 8084...
java -jar target\reporting-service-0.0.1-SNAPSHOT.jar
goto end

:docker
echo Ejecutando con Docker Compose...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta instalado
    pause
    exit /b 1
)
docker-compose up --build
goto end

:clean_build
echo Limpiando y compilando todo...
%MVN_CMD% clean install
echo [OK] Build completo exitoso!
goto end

:invalid
echo [ERROR] Opcion invalida
goto menu

:exit
echo Saliendo...
exit /b 0

:end
echo.
echo ==========================================
echo    Proceso completado
echo ==========================================
pause
