@ECHO OFF
if "%1"=="" goto ERROR
if "%2"=="" goto ERROR
if "%3"=="" goto ERROR
SET SAHI_HOME=..\..
SET USERDATA_DIR=..\
call setjava.bat
SET SCRIPTS_PATH=scripts
SET START_URL=%2
SET THREADS=5
SET SINGLE_SESSION=false
SET LOGS_INFO=html
SET SEND_EMAIL_REPORT=false
SET EMAIL_TRIGGER=success,failure
SET EMAIL_PROPERTIES=..\config\email.properties
SET EMAIL_PASSWORD_HIDDEN=true
REM SET CUSTOM_FIELDS=-customField customValue -anotherCustomField "another value"
REM LOGS_INFO format is type:filePath,type2,type3:filePath3
REM SET LOGS_INFO=html:D:/html,xml
java -cp %SAHI_HOME%\lib\ant-sahi.jar net.sf.sahi.test.TestRunner %CUSTOM_FIELDS% -test %SCRIPTS_PATH%/%1 -browserType %3 -logsInfo "%LOGS_INFO%" -baseURL "%START_URL%" -host localhost -port 9999 -threads %THREADS% -useSingleSession %SINGLE_SESSION% -sendEmail %SEND_EMAIL_REPORT% -emailTrigger "%EMAIL_TRIGGER%" -emailProperties "%EMAIL_PROPERTIES%" -emailPasswordHidden "%EMAIL_PASSWORD_HIDDEN%" -tags %4
goto :EOF

:ERROR
echo --
echo Usage: %0 ^<sah file^|suite file^> ^<startURL^> ^<browserType^> ^<tags^>
echo File path is relative to userdata/scripts
echo Multiple browsers can be specified using +. Eg. ie+firefox
echo tags are used only if the input suite is a csv file or a dd.csv file
echo --
echo Example:
echo %0 demo/demo.suite http://sahipro.com/demo/ firefox
echo %0 demo/sahi_demo.sah http://sahipro.com/demo/ ie
echo %0 demo/sahi_demo.sah http://sahipro.com/demo/ ie+firefox
echo %0 demo/demo.dd.csv http://sahipro.com/demo/ firefox "win||all"
echo %0 demo/testcases/testcases_sample.csv http://sahipro.com/demo/ ie "(user||admin)&&medium"
echo %0 demo/ddcsv/test.dd.csv http://sahipro.com/demo/ ie "(user||admin)&&medium"