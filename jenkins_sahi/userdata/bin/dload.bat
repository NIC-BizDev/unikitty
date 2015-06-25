@ECHO OFF
if "%1"=="" goto ERROR
if "%2"=="" goto ERROR
if "%3"=="" goto ERROR
if "%4"=="" goto ERROR
SET SAHI_HOME=..\..
SET USERDATA_DIR=..\
call setjava.bat
SET ORIG_SCRIPTS_PATH=../scripts/
SET ORIG_SCRIPTS_UDPATH=scripts/
SET COPIED_SCRIPTS_PATH=temp/scripts/copied/
SET START_URL=%3
REM SET NODES=localhost:9999,othermachine:9999,thirdmachine:9999
SET NODES=localhost:9999
REM java -cp %SAHI_HOME%\lib\ant-sahi.jar in.co.sahi.distributed.DSync -destFolder %COPIED_SCRIPTS_PATH% -origScriptsPath %ORIG_SCRIPTS_UDPATH% -originFolder %ORIG_SCRIPTS_UDPATH% -nodes "%NODES%" -ignorePattern ".*(svn|copied).*"
java -cp %SAHI_HOME%\lib\ant-sahi.jar in.co.sahi.distributed.DLoadRunner -origScriptsPath %ORIG_SCRIPTS_UDPATH% -copiedScriptsPath %COPIED_SCRIPTS_PATH% -noise %COPIED_SCRIPTS_PATH%/%1 -noiseBrowserType phantomjs -min 1 -max 9 -incrementBy 2 -interval 5 -subject %COPIED_SCRIPTS_PATH%/%2 -browserType %4 -baseURL "%START_URL%" -host localhost -port 9999 -nodes "%NODES%" -ignorePattern ".*(svn|copied).*"
goto :EOF

:ERROR
echo --
echo Usage: %0 ^<noise sah file^> ^<subject sah file^> ^<startURL^> ^<browserType^>
echo File path is relative to userdata/scripts
echo tags are used only if the input suite is a csv file
echo --
echo Example:
echo %0 demo/load/noise.sah demo/load/subject.sah http://sahipro.com/demo/training firefox
echo --