@echo off
cd /d %~dp0

@echo off
cd /d %~dp0

:: Close CMDs from previous run
taskkill /FI "WINDOWTITLE eq Compile" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Master" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq WorkerNode" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Reducer" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Manager" /T /F >nul 2>&1

:: Compile all Java files
start "Compile" cmd /k "javac *.java"

:: Wait 1 second to allow compilation to complete
timeout /t 2 /nobreak >nul

:: Start Master in a new cmd window
start "Master" cmd /k "java Master"

:: Start WorkerNode in a new cmd window
start "WorkerNode" cmd /k "java WorkerLauncher"

:: Start Reducer in a new cmd window
start "Reducer" cmd /k "java Reducer"

:: Wait another second for Master and WorkerNode to initialize
timeout /t 1 /nobreak >nul

:: Start Pelatis in a new cmd window
start "Manager" cmd /k "java Manager"
