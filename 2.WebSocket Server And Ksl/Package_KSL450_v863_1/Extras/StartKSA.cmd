@echo off
CLS
echo.
echo This will close all active LoadMaster and Levelmaster programs on this computer
echo Is this OK?
If not exist getne.exe goto NOGETNE
getne
If Errorlevel 1 Goto ABORT
cls
echo.
echo ksl450.exe is now terminating...
taskkill /f /im ksl450.exe
ping -n 2 localhost >nul
echo lr.exe is now terminating...
taskkill /f /im lr.exe
ping -n 2 localhost >nul
start "", "c:\program files\kockum sonics\ksl450\ksl450.exe"
:ABORT
CLS
echo.
echo Cancelled
Goto END
:NOGETNE
cls
echo.
echo No getne.exe
Goto END
:END
echo.
