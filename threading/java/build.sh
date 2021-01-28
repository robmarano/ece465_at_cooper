#!/bin/bash

# First ensure dependencies loaded since .m2 may be empty
mvn dependency:tree
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

#
# Config
#
APP_VERSION=$(cat ${APP_VERSION_FILE})

#mvn test
mvn install -Dmaven.test.skip=true
mvn package -N -P classpath-deps -Dmaven.test.skip=true

# Package runtimes with compiled and built libraries
#./build_app.sh

#echo "Copying job scripts for at scheduling to build/app/ ..."
#cp ./cicd/deploy.sh ./build/app/
#cp ./app/opt/start_app.sh ./build/app/
#echo "Done."

echo "Done building APP."
