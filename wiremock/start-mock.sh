#!/usr/bin/env bash
# WireMock mock server for NoviCloud SDK demo
# Requires: wiremock-standalone-3.x.x.jar in this folder
# Download: https://wiremock.org/docs/standalone/java-jar/

JAR="wiremock-standalone.jar"
DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$DIR/$JAR" ]; then
    echo "ERROR: $JAR not found."
    echo "Download from https://wiremock.org/docs/standalone/java-jar/"
    echo "and place it in this directory as \"$JAR\""
    exit 1
fi

echo "Starting WireMock mock server on http://localhost:4010 ..."
java -jar "$DIR/$JAR" --port 4010 --root-dir "$DIR" --verbose
