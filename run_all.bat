@echo off
cd /d %~dp0


:: Compile all Java files
start "Compile" cmd /k "javac *.java"

:: Wait 1 second to allow compilation to complete
:: timeout /t 2 /nobreak >nul

:: Start Master in a new cmd window
start "Master" cmd /k "java Master"

:: Start WorkerNode in a new cmd window
start "Worker 0" cmd /k "java WorkerNode 0 4442"
start "Worker 1" cmd /k "java WorkerNode 1 4443"
:: start "Worker 2" cmd /k "java WorkerNode 2 4444"


:: Start Reducer in a new cmd window
start "Reducer" cmd /k "java Reducer"

:: Wait another second for Master and WorkerNode to initialize
:: timeout /t 1 /nobreak >nul

:: Start Pelatis in a new cmd window
:: start "Manager" cmd /k "java Manager"
