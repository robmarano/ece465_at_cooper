#!/bin/bash
#
# APP Java

APP_EXECUTABLE=app
APP_JAR_VERSION_NUMBER=$(mvn help:evaluate -Dexpression=project.version | grep -e '^[^\[]')
APP_VERSION_FILE=../app.version
if [ ! -f "${APP_VERSION_FILE}" ]; then
    echo "APP Version file DOES NOT exist. CANNOT proceed with build."
    exit 1
fi

#
# Config
#
APP_VERSION=$(cat ${APP_VERSION_FILE})
APP_PROP_FILE_NAME=${APP_EXECUTABLE}.properties
APP_PROP_FILE=./opt/${APP_PROP_FILE_NAME}
APP_TEMPLATE_PROP_FILE=${APP_EXECUTABLE}/opt/${APP_EXECUTABLE}.properties-template

[ -e ${APP_PROP_FILE} ] && echo "Deleting ${APP_PROP_FILE} ..." && /bin/rm -f ${APP_PROP_FILE} && echo "Done."
cat ${APP_TEMPLATE_PROP_FILE} | sed -e "s#THEVERSION#${APP_VERSION}#g" > ${APP_PROP_FILE}
echo "${APP_PROP_FILE}"
cat "${APP_PROP_FILE}"

#
# Build & Deploy
#
echo "Building APP ${APP_EXECUTABLE} ${APP_VERSION}..."

#
# Deploy
#
LOCAL_TARGET_ROOT=../build
LOCAL_TARGET_DIR=${LOCAL_TARGET_ROOT}/app/${APP_EXECUTABLE}

TARGET_FILE_JAR="${APP_EXECUTABLE}/target/${APP_EXECUTABLE}-${APP_JAR_VERSION_NUMBER}.jar"
TARGET_FILES_SH="${APP_EXECUTABLE}/opt/run-${APP_EXECUTABLE}.sh"
TARGET_FILE_LOG4J="${APP_EXECUTABLE}/opt/log4j.properties"

echo "Deploying locally to \"${LOCAL_TARGET_DIR}\"";
mkdir -p ${LOCAL_TARGET_DIR}
cp ${TARGET_FILE_JAR} ${LOCAL_TARGET_DIR}
cp ${TARGET_FILE_LOG4J} ${LOCAL_TARGET_DIR}
cp ${APP_PROP_FILE} ${LOCAL_TARGET_DIR}
#NEEDED_JARS="./commons/target/commons-${APP_JAR_VERSION_NUMBER}.jar ./target/classpathDependencies/amazon-kinesis-client-1.8.9.jar ./target/classpathDependencies/amazon-kinesis-producer-0.12.8.jar ./target/classpathDependencies/jsqlparser-1.1.jar ./target/classpathDependencies/jersey-bundle-1.19.1.jar" # place spaces between jar files, e.g., "z.jar x.jar y.jar"
#for jars in ${NEEDED_JARS}
#do
#  cp ${jars} ${LOCAL_TARGET_DIR}
#done
for driver in ${TARGET_FILES_SH}
do
  cp ${driver} ${LOCAL_TARGET_DIR}
done

# Remove dynamically built properties file
[ -e ${APP_PROP_FILE} ] && echo "Deleting ${APP_PROP_FILE} ..." && /bin/rm -f ${APP_PROP_FILE} && echo "Done."

echo "Done building APP Engine Application."
exit 0
