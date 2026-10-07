@echo off
title CareerShield AI - Spring Boot Server
echo ======================================================================
echo           CareerShield AI - Fake Internship & Job Detection
echo                    "Verify Before You Apply."
echo ======================================================================
echo.

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "M2_HOME=C:\Users\sujit\.maven\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%"

echo [1/2] Checking Java and Maven...
java -version
mvn -version

echo.
echo [2/2] Starting Spring Boot Web Server on http://localhost:8080 ...
echo Press Ctrl+C anytime to stop the server.
echo.

mvn spring-boot:run -o

pause
