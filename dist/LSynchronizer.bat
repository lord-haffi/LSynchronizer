@echo off
color 0a

IF NOT "%1"=="" goto 1

java -jar "%~dp0LSynchro_app.jar"
REM start javaw.exe "%~dp0Synchronisation_v2.0.jar"
goto end

:1
java -jar "%~dp0LSynchro_app.jar" %1
goto end

:end
REM pause