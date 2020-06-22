@echo off
CLS
SETLOCAL
set V_COMMAND="C:\Program Files (x86)\Kockum Sonics\KSL450\CliReg32.exe" "C:\Program Files (x86)\Kockum Sonics\KSL450\ksl450.vbr" -s 192.168.1.1 -t "C:\Program Files (x86)\Kockum Sonics\KSL450\ksl450.tlb" -l -p ncacn_ip_tcp -q -nologo -a 1 -d
%V_COMMAND%
ENDLOCAL
EXIT