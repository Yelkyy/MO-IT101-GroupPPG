@echo off
echo Compiling Java files...

:: Find all Java files inside src/ and compile them to bin/
javac -cp "lib/*;src" -d bin src\model\*.java src\repository\*.java src\service\*.java src\App.java

if %errorlevel% neq 0 (
    echo Compilation failed. Fix errors and try again.
    pause
    exit /b %errorlevel%
)

echo Compilation successful. Running program...
java -cp "bin;lib/*" App

echo Program execution finished.
pause
