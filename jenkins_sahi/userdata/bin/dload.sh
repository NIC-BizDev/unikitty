#!/bin/bash
if [ $# -ne 4 ] 
then
echo "Usage: ./dload.sh <noise sah file> <subject sah file> <startURL> <browserType>"
echo "File path is relative to userdata/scripts"
echo "--"
echo "Example:" 
echo "./dload.sh demo/load/noise.sah demo/load/subject.sah http://sahipro.com/demo/training firefox"
echo "--"

else
export SAHI_HOME=../..
export USERDATA_DIR=../
. ./setjava.sh
export ORIG_SCRIPTS_PATH=../scripts/
export ORIG_SCRIPTS_UDPATH=scripts/
export COPIED_SCRIPTS_PATH=temp/scripts/copied/
export START_URL=$3
# SET NODES=localhost:9999,othermachine:9999,thirdmachine:9999
export NODES=localhost:9999
# java -cp $SAHI_HOME/lib/ant-sahi.jar in.co.sahi.distributed.DSync -originFolder $ORIG_SCRIPTS_PATH -destFolder $COPIED_SCRIPTS_PATH -nodes "$NODES" -ignorePattern ".*(svn|copied).*"
java -cp $SAHI_HOME/lib/ant-sahi.jar in.co.sahi.distributed.DLoadRunner -origScriptsPath $ORIG_SCRIPTS_UDPATH -copiedScriptsPath $COPIED_SCRIPTS_PATH -noise $COPIED_SCRIPTS_PATH/$1 -noiseBrowserType phantomjs -min 1 -max 9 -incrementBy 2 -interval 5 -subject $COPIED_SCRIPTS_PATH/$2 -browserType $4 -baseURL "$START_URL" -host localhost -port 9999 -nodes "$NODES" -ignorePattern ".*(svn|copied).*"
fi
