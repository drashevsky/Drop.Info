#!/bin/bash

# Requires jre and jdk to be installed. Chmod +x this file if it fails to start.

# Get dependencies
for f in deps/*; do CLASSPATH=$CLASSPATH:$PWD/$f; done

# Build server backend
javac -classpath "$CLASSPATH" src/*.java
mv src/*.class build/

# Create directories
if [ ! -d "build/images" ]; then
	mkdir build/images
fi

if [ ! -d "build/profiles" ]; then
  	mkdir build/profiles
fi

if [ ! -d "build/static" ]; then
  	mkdir build/static
fi

if [ ! -d "build/views" ]; then
  	mkdir build/views
fi

# Copy dependencies
cp deps/*.jar build/

# Copy run script
cp runscripts/runserver.sh build
sudo chmod +x build/runserver.sh

# Copy web code
cp src/web/*.css build/static
cp src/web/*.js build/static
cp src/web/*.html build/views

# Report status
echo Done with build, call runserver.sh in build folder to start server.