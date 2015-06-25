#!/bin/bash
if [ $# -lt 3 ] 
then
echo "Usage: ./testrunner.sh <sah file|suite file> <startURL> <browserType> <tags>"
echo "File path is relative to userdata/scripts"
echo Multiple browsers can be specified using +. Eg. firefox+safari
echo tags are used only if the input suite is a csv file or a dd.csv file
echo "Example:" 
echo "$0 demo/demo.suite http://sahipro.com/demo/ firefox"
echo "$0 demo/sahi_demo.sah http://sahipro.com/demo/ firefox"
echo "$0 demo/sahi_demo.sah http://sahipro.com/demo/ firefox+safari"
echo "$0 demo/demo.dd.csv http://sahipro.com/demo/ firefox \"linux||all\""
echo "$0 demo/testcases/testcases_sample.csv http://sahipro.com/demo/ firefox \"(admin||user)&&medium\""
echo "$0 demo/ddcsv/test.dd.csv http://sahipro.com/demo/ firefox \"(admin||user)&&medium\""
else

export SAHI_HOME=../..
export USERDATA_DIR=../
export SCRIPTS_PATH=scripts/$1
export BROWSER=$3
export START_URL=$2
export THREADS=5
export SINGLE_SESSION=false
export LOGS_INFO=html
export SEND_EMAIL_REPORT=false
export EMAIL_TRIGGER=success,failure
export EMAIL_PROPERTIES=../config/email.properties
export EMAIL_PASSWORD_HIDDEN=true
. ./setjava.sh
#export CUSTOM_FIELDS=-customField customValue -anotherCustomField "another value"
java -cp $SAHI_HOME/lib/ant-sahi.jar net.sf.sahi.test.TestRunner $CUSTOM_FIELDS -test $SCRIPTS_PATH -browserType "$BROWSER" -logsInfo "$LOGS_INFO" -baseURL $START_URL -host localhost -port 9999 -threads $THREADS -useSingleSession $SINGLE_SESSION -sendEmail $SEND_EMAIL_REPORT -emailTrigger "$EMAIL_TRIGGER" -emailProperties "$EMAIL_PROPERTIES" -emailPasswordHidden "$EMAIL_PASSWORD_HIDDEN" -tags $4
fi
