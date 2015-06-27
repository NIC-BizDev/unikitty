@ECHO OFF
if "%1"=="" goto ERROR
if "%2"=="" goto ERROR
if "%3"=="" goto ERROR
if "%4"=="" goto ERROR
call setjava.bat
SET SAHI_HOME=..\..
SET NODE=%1
SET DOWNLOAD_PATH=%2
SET INSTALL_PATH=%3
SET SAHI_PRO_INSTALLER_JAR=%4
java -cp %SAHI_HOME%\lib\sahi.jar in.co.sahi.command.UpgradeSahi -node %NODE% -downloadPath %DOWNLOAD_PATH% -installPath %INSTALL_PATH% -sahiProInstallerJar %SAHI_PRO_INSTALLER_JAR%
goto :EOF

:ERROR
echo --
echo Usage: %0 ^<machine name^>^<downlaod directory path of remote machine^> ^<installed sahi_pro path^> ^<sahi_pro installer file name^>
echo --
echo Example:
echo %0 localhost D:\Downloads C:\Users\Tyto\sahi_pro install_sahi_pro_v5100_20131202.jar