@Echo Off
CLS
ping -n 3 localhost >nul
Start "" "%Programfiles%\Kockum Sonics\XXXX\LRT\lr.exe"
ping -n 5 localhost >nul
Start "" "%Programfiles%\Kockum Sonics\KSL450\ksl450.exe"
