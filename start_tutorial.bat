@echo off
setlocal
set LIGHT_GREEN=0A
set LIGHT_BLUE=09
set CYAN=0B
set RED=0C

SETLOCAL EnableDelayedExpansion
for /F "tokens=1,2 delims=#" %%a in ('"prompt #$H#$E# & echo on & for %%b in (1) do rem"') do (
  set "DEL=%%a"
)

if "%GS_HOME%" == "" (
    GOTO GS_HOME_NOT_SET
)

IF NOT EXIST "%GS_HOME%\tools\groovy\bin\groovy" GOTO GROOVY_NOTFOUND_ERROR

:loop
set case=
cls
echo(
call :ColorText %CYAN% "ooooooo  ooooo       .o.       ooooooooo." NEWLINE
call :ColorText %CYAN% " `8888    d8'       .888.      `888   `Y88" NEWLINE
call :ColorText %CYAN% "   Y888..8P        .8'888.      888   .d88" NEWLINE
call :ColorText %CYAN% "    `8888'        .8' `888.     888ooo88P'" NEWLINE
call :ColorText %CYAN% "   .8PY888.      .88ooo8888.    888" NEWLINE
call :ColorText %CYAN% "  d8'  `888b    .8'     `888.   888" NEWLINE
call :ColorText %CYAN% "o888o  o88888o o88o     o8888o o888o" NEWLINE
echo(
echo XAP Interactive Tutorial
echo Choose one of the options for tutorial bellow:
call :ColorText %LIGHT_GREEN% "1]"
echo  XAP Demo - Write/Read to/from myDataGrid space
call :ColorText %LIGHT_GREEN% "2]"
echo  XAP Interactive Shell
call :ColorText %LIGHT_GREEN% "3]"
echo  XAP 10 New API
call :ColorText %LIGHT_GREEN% "0]"
echo  exit
set /p case= Your choice:

:choiceloop
IF "%case%" == "0" (
    goto :end
)
IF "%case%" == "1" (
        call %GS_HOME%\tools\groovy\bin\groovy.bat XAPDemoScript.groovy
        pause
        goto :loop
)
IF "%case%" == "2" (
            cls
            call %GS_HOME%\tools\groovy\bin\groovysh.bat
            pause
            goto :loop
)
IF "%case%" == "3" (
        call %GS_HOME%\tools\groovy\bin\groovy.bat XAP10NewAPI.groovy
        pause
        goto :loop
)

set /p case= Invalid option, please try again:
goto :choiceloop

:end
call :ColorText %CYAN% "Thank you for using XAP Interactive Tutorial" NEWLINE
endlocal
pause
goto :EOF

:GROOVY_NOTFOUND_ERROR
set MSG="GS_HOME environment variable is not configured properly or groovy cannot be found, please make sure it points to XAP home directory."
call :ColorText %RED% %MSG% NEWLINE
endlocal
pause
goto :EOF

:GS_HOME_NOT_SET
set MSG="GS_HOME environment variable is not set"
call :ColorText %RED% %MSG% NEWLINE
pause
goto :EOF

:ColorText
echo off
<nul set /p ".=%DEL%" > "%~2"
findstr /v /a:%1 /R "^$" "%~2" nul
del "%~2" > nul 2>&1
IF NOT "%~3" == "" echo(
goto :eof