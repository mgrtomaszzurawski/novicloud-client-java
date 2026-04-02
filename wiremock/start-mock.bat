@echo off
REM WireMock mock server for NoviCloud SDK demo
REM Requires: wiremock-standalone-3.x.x.jar in this folder
REM Download: https://wiremock.org/docs/standalone/java-jar/

set JAR=wiremock-standalone.jar

if not exist %JAR% (
    echo ERROR: %JAR% not found.
    echo Download from https://wiremock.org/docs/standalone/java-jar/
    echo and place it in this directory as "%JAR%"
    exit /b 1
)

echo Starting WireMock mock server on http://localhost:4010 ...
java -jar %JAR% --port 4010 --root-dir "%~dp0" --verbose
