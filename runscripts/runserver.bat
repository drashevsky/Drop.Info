@echo off
setlocal EnableDelayedExpansion
for /r %%i in (*.class) do set CLASSPATH=%%i;!CLASSPATH!
for /r %%i in (*.jar) do set CLASSPATH=%%i;!CLASSPATH!
java -classpath "!CLASSPATH!" Server
pause