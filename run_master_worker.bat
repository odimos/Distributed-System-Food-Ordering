@echo off
cd /d %~dp0

:: Compile all Java files
start "Compile" cmd /k "javac *.java"

:: Wait 1 second to allow compilation to complete
timeout /t 2 /nobreak >nul

:: Start Master in a new cmd window
start "Master" cmd /k "java Master"

:: Start WorkerNode in a new cmd window
start "WorkerNode" cmd /k "java WorkerLauncher"