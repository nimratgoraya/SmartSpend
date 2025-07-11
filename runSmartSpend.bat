@echo off
cd /d "%~dp0src"

echo Compiling Java files...
javac --module-path "C:\Users\ASUS\OneDrive\Desktop\javaFX setup\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/mysql-connector-j-9.3.0.jar;.." *.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed. Fix the errors and try again.
    pause
    exit /b
)

echo ✅ Compilation successful. Running LoginPage...
java --module-path "C:\Users\ASUS\OneDrive\Desktop\javaFX setup\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/mysql-connector-j-9.3.0.jar;.." LoginPage

pause
