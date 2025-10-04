#!/bin/bash

# Simple build and run script for the multithreading demos
# Usage: ./build.sh [demo_name]

# Create necessary directories
mkdir -p out/classes

echo "Compiling Java files..."

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d out/classes -cp src/main/java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"

    if [ -z "$1" ]; then
        echo "Running main demo runner..."
        cd out/classes
        java com.multithreading.MultithreadingDemoRunner
    else
        echo "Running specific demo: $1"
        cd out/classes
        java com.multithreading.$1
    fi
else
    echo "Compilation failed!"
    exit 1
fi
