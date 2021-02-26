#!/bin/bash

APP="imaging"

# First ensure dependencies loaded since .m2 may be empty
mvn dependency:tree -Ddetail=true
mvn help:evaluate -Dexpression=project.version

# Clean repo from builds
./clean.sh

#
# Config
#
APP_VERSION_FILE=./app.version

THEUSER=$(/usr/bin/whoami)
NOW=$(date "+%Y%m%d%H%M%S")
APP_MAVEN_VERSION=$(mvn help:evaluate -Dexpression=project.version | grep -e '^[^\[]')
APP_GIT_VERSION=$(git rev-parse --abbrev-ref HEAD)

APP_VERSION=${APP_MAVEN_VERSION}-${APP_GIT_VERSION}_${NOW}_${THEUSER}
echo ${APP_VERSION} > ${APP_VERSION_FILE}

echo "LOCALLY building runtime to local folder: ./build ..."
echo "Version = ${APP_VERSION}"

if [ ! -f "${APP_VERSION_FILE}" ]; then
    echo "APP Version file DOES NOT exist. CANNOT proceed with build."
    exit 1
fi


# APP Java

APP_EXECUTABLE=${APP}
APP_JAR_VERSION_NUMBER=$(mvn help:evaluate -Dexpression=project.version | grep -e '^[^\[]')
APP_VERSION_FILE=./app.version
if [ ! -f "${APP_VERSION_FILE}" ]; then
    echo "APP Version file DOES NOT exist. CANNOT proceed with build."
    exit 1
fi

#
# Config
#
APP_VERSION=$(cat ${APP_VERSION_FILE})
APP_PROP_FILE_NAME=app.properties
APP_PROP_FILE=${APP_EXECUTABLE}/opt/${APP_PROP_FILE_NAME}
APP_TEMPLATE_PROP_FILE=./opt/${APP_EXECUTABLE}.properties-template

[ -e ${APP_PROP_FILE} ] && echo "Deleting ${APP_PROP_FILE} ..." && /bin/rm -f ${APP_PROP_FILE} && echo "Done."
cat ${APP_TEMPLATE_PROP_FILE} | sed -e "s#THEVERSION#${APP_VERSION}#g" > ${APP_PROP_FILE}
echo "${APP_PROP_FILE}"
cat "${APP_PROP_FILE}"

#
# Build & Deploy
#
echo "Building APP ${APP_EXECUTABLE} ${APP_VERSION}..."
mvn install -Dmaven.test.skip=true
mvn package -N -Dmaven.test.skip=true
#
# Deploy
#
LOCAL_TARGET_ROOT=./build
LOCAL_TARGET_DIR=${LOCAL_TARGET_ROOT}/app/${APP_EXECUTABLE}

TARGET_FILE_JAR="${APP_EXECUTABLE}/target/${APP_EXECUTABLE}-${APP_JAR_VERSION_NUMBER}-jar-with-dependencies.jar"
TARGET_FILES_SH="${APP_EXECUTABLE}/opt/run-${APP_EXECUTABLE}.sh"
TARGET_FILE_LOG4J="${APP_EXECUTABLE}/opt/log4j.properties"

echo "Done building APP."
