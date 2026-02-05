#!/bin/bash

# ===================================
# Build & Run Chatter-Client Frontend
# ===================================

echo Building project with Gradle...
./gradlew installDist

if [ $? -ne 0 ]; then
  echo Gradle build failed. Exiting. Dumbass.
  exit 1
fi

echo Running Chatter-Client...

# Change this path if the app name ever changes
./frontend/build/install/frontend/bin/frontend



