@echo off
REM ===========================
REM Build & Run Chatter-Client
REM ===========================

echo Building project with Gradle...
call .\gradlew.bat :frontend:installDist

IF %ERRORLEVEL% NEQ 0 (
    echo Gradle build failed. Exiting. Dumbass.
    exit /b 1
)

echo Running Chatter-Client...
call .\frontend\build\install\frontend\bin\frontend.bat
pause
