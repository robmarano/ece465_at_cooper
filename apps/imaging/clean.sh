#!/bin/bash

APP=imaging
APP_VERSION_FILE=./${APP}.version

# set MAVEN_OPTS
#MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
MAVEN_OPTS=-Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

MVN_CMD="mvn -Dorg.slf4j.simpleLogger.defaultLogLevel=ERROR"

echo "Removing built project artifacts..."
/bin/rm -rf ./build
echo "Done."
echo "Maven build clean..."
${MVN_CMD} clean

[ -e ${APP_VERSION_FILE} ] && echo "Deleting ${APP_VERSION_FILE} ..." && /bin/rm -f ${APP_VERSION_FILE} && echo "Deleted."

echo "Done."
