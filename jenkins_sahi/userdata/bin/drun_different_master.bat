@ECHO OFF
REM This batch file should be used only if you plan to use a different Master machine.
REM Sahi may or may not be installed on the Initiator machine (where this batch file is run). The following directory structure is assumed so that it is applicable for both scenarios (whether Sahi is installed or not)
REM
REM <TOP_LEVEL_FOLDER>
REM		config
REM			email.properties
REM 	lib
REM			ant-sahi.jar
REM		logs
REM		userdata
REM			bin
REM				drun_different_master.bat
REM				setjava.bat
REM			scripts
REM				<ALL YOUR SCRIPT FOLDERS AND SCRIPTS>

if "%1"=="" goto ERROR
if "%2"=="" goto ERROR
if "%3"=="" goto ERROR

SET TOP_LEVEL_FOLDER=..\..
call setjava.bat

REM MASTER_HOST refers to the machine that serves as the Master
REM IMPORTANT: Make sure you change MASTER_HOST to the actual machine name.
SET MASTER_HOST=QALaptop1
SET MASTER_PORT=9999

REM initiatorOriginFolder SHOULD be relative to drun_different_master.bat OR an absolute path on the Initiator machine.
REM It contains ALL the scripts that are to be run.
SET INITIATOR_ORIGIN_FOLDER=%TOP_LEVEL_FOLDER%/userdata/scripts

REM MASTER_STAGING_PATH refers to the Staging folder on the Master machine to which the contents of INITIATOR_ORIGIN_FOLDER will be first synced to.
REM Distribution of scripts will happen from MASTER_STAGING_PATH.
REM	This should be relative to the userdata folder, or an absolute path on the Master machine
SET MASTER_STAGING_PATH=temp/scripts/staging

REM COPIED_SCRIPTS_PATH should be relative to the userdata folder. This is the folder to which the scripts from MASTER_STAGING_PATH will be synced to, on all nodes
SET COPIED_SCRIPTS_PATH=temp/scripts/copied/

REM START_URL refers to the start url against which the suite will be run. This is to be passed as the 2nd parameter to drun.
SET START_URL=%2

REM Scripts will be distributed across all the nodes. The nodes may or may not include the Master machine
REM SET NODES=machine2:9999,machine3:9999,machine4:9999
SET NODES=machine2:9999

SET NODES_FILEPATH=

REM Set SEND_EMAIL_REPORT to true if email is to be sent at the end of a run
SET SEND_EMAIL_REPORT=false
REM EMAIL_TRIGGER indicates the trigger when email should be sent. Possible values are success OR failure OR success,failure
SET EMAIL_TRIGGER=success,failure
REM email.properties contains the details needed for sending the email
SET EMAIL_PROPERTIES=..\config\email.properties
REM Set EMAIL_PASSWORD_HIDDEN to true to prevent the password from getting logged
SET EMAIL_PASSWORD_HIDDEN=true

REM Uncomment the following line to set custom fields. Replace the custom field keys and values as appropriate
REM SET CUSTOM_FIELDS=-customField customValue -anotherCustomField "another value"

REM Uncomment the following line to set the userDefinedId. Replace the value as appropriate. The key should remain as userDefinedId
REM SET USER_DEFINED_ID=-userDefinedId  "Some Id"

REM Sahi can set offline logs to be generated in xml,html,junit,tm6 and excel types. The default type is html. These logs will be generated on the Master and pulled back to the Initiator, since the user would want to store the logs on the Initiator.
REM If you do not want offline logs, comment out everything between the lines "REM Offline logs start" and "REM Offline logs end".  
REM Offline logs start

SET DATESTAMP=%DATE:~10,4%_%DATE:~4,2%_%DATE:~7,2%
SET TIMESTAMP=%TIME:~0,2%_%TIME:~3,2%_%TIME:~6,2%
SET DATEANDTIME=%DATESTAMP%_%TIMESTAMP%

REM MASTER_HTMLLOGS_DIR should be relative to the userdata folder or it should be an absolute path on the Master machine
SET MASTER_HTMLLOGS_DIR=logs/temp/html/%DATEANDTIME%

REM LOGS_INFO format is type:filePath,type2,type3:filePath3
SET LOGS_INFO=html:%MASTER_HTMLLOGS_DIR%

REM INITIATOR_OUTPUT_HTMLLOGS_DIR should be relative to the Ant folder (folder containing the Ant xml) OR an absolute path on the Initiator machine.
REM This is where the html logs will be copied back to on the Initiator machine
SET INITIATOR_OUTPUT_HTMLLOGS_DIR=%TOP_LEVEL_FOLDER%/logs/html/%DATEANDTIME%

REM If you intend to log other types, do it on the same lines as the HTML logs
REM Offline logs end

SET IGNORE_PATTERN=".*(svn|copied).*"

REM First sync the scripts to a staging folder on the Master
java -cp %TOP_LEVEL_FOLDER%\lib\ant-sahi.jar in.co.sahi.distributed.DSync -originFolder %INITIATOR_ORIGIN_FOLDER% -destFolder %MASTER_STAGING_PATH% -nodes "%MASTER_HOST%:%MASTER_PORT%" -ignorePattern %IGNORE_PATTERN%

REM Now perform the distributed run
java -cp %TOP_LEVEL_FOLDER%\lib\ant-sahi.jar in.co.sahi.distributed.DSahiRunner -originalSuitePath %MASTER_STAGING_PATH%/%1 -origScriptsPath %MASTER_STAGING_PATH% -copiedScriptsPath %COPIED_SCRIPTS_PATH% -ignorePattern %IGNORE_PATTERN% %CUSTOM_FIELDS% %USER_DEFINED_ID% -suite %COPIED_SCRIPTS_PATH%/%1 -browserType %3 -logsInfo "%LOGS_INFO%" -baseURL "%START_URL%" -host %MASTER_HOST% -port %MASTER_PORT% -nodes "%NODES%" -nodesFilePath "%NODES_FILEPATH%" -sendEmail %SEND_EMAIL_REPORT% -emailTrigger "%EMAIL_TRIGGER%" -emailProperties "%EMAIL_PROPERTIES%" -emailPasswordHidden "%EMAIL_PASSWORD_HIDDEN%" -tags %4

REM Pull the HTML logs from the Master onto the Initiator machine
java -cp %TOP_LEVEL_FOLDER%\lib\ant-sahi.jar in.co.sahi.distributed.DPull -sourceHost %MASTER_HOST% -sourcePort %MASTER_PORT% -originFolder %MASTER_HTMLLOGS_DIR% -destFolder %INITIATOR_OUTPUT_HTMLLOGS_DIR% -ignorePattern %IGNORE_PATTERN%

REM If you have created logs of other types such as JUnit, add a DPull call for that logs type too

goto :EOF

:ERROR
echo --
echo NOTE: Use this batch file only if you plan to use a different Master machine. Else, use drun.bat instead.
echo --
echo Usage: %0 ^<sah file^|suite file^> ^<startURL^> ^<browserType^> ^<tags^>
echo File path is relative to userdata/scripts
echo Multiple browsers can be specified using +. Eg. ie+firefox
echo tags are used only if the input suite is a csv file
echo --
echo Example:
echo %0 demo/demo.suite http://sahi.co.in/demo/ firefox
echo %0 demo/sahi_demo.sah http://sahi.co.in/demo/ ie
echo %0 demo/sahi_demo.sah http://sahi.co.in/demo/ ie+firefox
echo %0 demo/testcases/testcases_sample.csv http://sahi.co.in/demo/ ie "(user||admin)&&medium"
echo %0 demo/ddcsv/test.dd.csv http://sahi.co.in/demo/ ie "(user||admin)&&medium"	
echo --