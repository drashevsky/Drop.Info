@echo off

REM Requires jre and jdk to be installed, java and javac added to system path.

REM Get dependencies
for /r %%i in (deps\*) do set CLASSPATH=%%i;%CLASSPATH%

REM Build server backend
javac src\*.java
move src\*.class build

REM Create directories
if not exist build\images (
	mkdir build\images
)

if not exist build\profiles (
	mkdir build\profiles
)

if not exist build\static (
	mkdir build\static
)

if not exist build\views (
	mkdir build\views
)

REM Copy dependencies
copy deps\*.jar build

REM Copy run script
copy runscripts\runserver.bat build

REM Copy web code
copy src\web\*.css build\static
copy src\web\*.js build\static
copy src\web\*.html build\views

REM Report status
echo Done with build, call runserver.bat in build folder to start server.
pause
