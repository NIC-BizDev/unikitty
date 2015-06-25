#!/bin/bash

# This shell script should be used only if you are running it on the Master machine.

if [ $# -lt 3 ] 
then
echo "Usage: drun.sh <sah file|suite file> <startURL> <browserType> <tags>"
echo "File path is relative to userdata/scripts"
echo "Example:" 
echo "$0 demo/demo.suite http://sahi.co.in/demo/ firefox"
echo "$0 demo/sahi_demo.sah http://sahi.co.in/demo/ firefox"
echo "$0 demo/sahi_demo.sah http://sahi.co.in/demo/ firefox+safari"
echo "$0 demo/testcases/testcases_sample.csv http://sahi.co.in/demo/ firefox \"(admin||user)&&medium\""
echo "$0 demo/ddcsv/test.dd.csv http://sahi.co.in/demo/ firefox \"(admin||user)&&medium\""
else

export SAHI_HOME=../..
. ./setjava.sh

# REM SOURCE_SCRIPTS_PATH should be relative to the userdata folder OR it should be an absolute path. It contains ALL the scripts that are to be run.
export SOURCE_SCRIPTS_PATH=scripts/

# COPIED_SCRIPTS_PATH should be relative to the userdata folder. This is the folder to which the scripts from SOURCE_SCRIPTS_PATH will be synced to, on all nodes
export COPIED_SCRIPTS_PATH=temp/scripts/copied/

# START_URL refers to the start url against which the suite will be run. This is to be passed as the 2nd parameter to drun.
export START_URL=$2

# Scripts will be distributed across all the nodes. The nodes may or may not include the Master machine, i.e. localhost
# SET NODES=localhost:9999,othermachine:9999,thirdmachine:9999
export NODES=localhost:9999

export NODES_FILEPATH=

export BROWSER=$3

# Set SEND_EMAIL_REPORT to true if email is to be sent at the end of a run
export SEND_EMAIL_REPORT=false
# EMAIL_TRIGGER indicates the trigger when email should be sent. Possible values are success OR failure OR success,failure
export EMAIL_TRIGGER=success,failure
# email.properties contains the details needed for sending the email
export EMAIL_PROPERTIES=../config/email.properties
# Set EMAIL_PASSWORD_HIDDEN to true to prevent the password from getting logged
export EMAIL_PASSWORD_HIDDEN=true

# Uncomment the following line to set custom fields. Replace the custom field keys and values as appropriate
# export CUSTOM_FIELDS=-customField customValue -anotherCustomField "another value"

# Uncomment the following line to set the userDefinedId. Replace the value as appropriate. The key should remain as userDefinedId
# export USER_DEFINED_ID=-userDefinedId  "Some Id"

# LOGS_INFO format is type:filePath,type2,type3:filePath3
# export LOGS_INFO=html:D:/html,xml
export LOGS_INFO=html

java -cp $SAHI_HOME/lib/ant-sahi.jar in.co.sahi.distributed.DSahiRunner -originalSuitePath $SOURCE_SCRIPTS_PATH/$1 -origScriptsPath $SOURCE_SCRIPTS_PATH -copiedScriptsPath $COPIED_SCRIPTS_PATH -ignorePattern ".*(svn|copied).*" $CUSTOM_FIELDS $USER_DEFINED_ID -suite $COPIED_SCRIPTS_PATH/$1 -browserType "$BROWSER" -logsInfo "$LOGS_INFO" -baseURL $START_URL -host localhost -port 9999 -nodes "$NODES" -nodesFilePath "$NODES_FILEPATH" -sendEmail $SEND_EMAIL_REPORT -emailTrigger "$EMAIL_TRIGGER" -emailProperties "$EMAIL_PROPERTIES" -emailPasswordHidden "$EMAIL_PASSWORD_HIDDEN" -tags $4
fi
