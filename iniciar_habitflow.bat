@echo off
chcp 65001 > nul
title HabitFlow: Programa de Hábitos de Estudio (21 Días)
echo =======================================================================
echo   Iniciando HabitFlow: Programa de Hábitos de Estudio (21 Días)
echo =======================================================================
cd /d "%~dp0"
start "" "%~dp0index.html"
echo.
echo La aplicación web se ha abierto directamente en tu navegador.
echo Si deseas ejecutar el servidor web local en el puerto 8080:
echo.
echo [1] Iniciar servidor local (http://localhost:8080/)
echo [2] Salir
echo.
set /p opt="Elige una opción (1 o 2): "
if "%opt%"=="1" (
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start_server.ps1"
)
exit
