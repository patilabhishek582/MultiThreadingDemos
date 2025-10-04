@echo off
REM Windows batch file to compile and run the multithreading demos
REM Usage: build.bat [demo_name]

REM Create necessary directories
if not exist "out\classes" mkdir out\classes

echo Compiling Java files...

REM Compile all Java files
javac -d out\classes -cp src\main\java src\main\java\com\multithreading\*.java src\main\java\com\multithreading\creation\*.java src\main\java\com\multithreading\communication\*.java src\main\java\com\multithreading\problems\*.java

if %ERRORLEVEL% equ 0 (
    echo Compilation successful!

    if "%1"=="" (
        echo Running main demo runner...
        cd out\classes
        java com.multithreading.MultithreadingDemoRunner
    ) else (
        echo Running specific demo: %1
        cd out\classes
        java com.multithreading.%1
    )
) else (
    echo Compilation failed!
    pause
    exit /b 1
)

pause
