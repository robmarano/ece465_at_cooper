#!/bin/bash

APP="imaging"

# set MAVEN_OPTS
#MAVEN_OPTS="-Dorg.slf4j.simpleLogger.defaultLogLevel=warn"
MAVEN_OPTS=-Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

MVN_CMD="mvn -Dorg.slf4j.simpleLogger.defaultLogLevel=ERROR"

# First ensure dependencies loaded since .m2 may be empty
${MVN_CMD} dependency:tree -Ddetail=true
${MVN_CMD} help:evaluate -Dexpression=project.version

# Clean repo from builds
./clean.sh

#
# Config
#
THEUSER=$(/usr/bin/whoami)
NOW=$(date "+%Y%m%d%H%M%S")
APP_VERSION_FROM_MAVEN=$(mvn help:evaluate -Dexpression=project.version | grep -e '^[^\[]')
echo ${APP_VERSION_FROM_MAVEN}
APP_BRANCH_FROM_GIT=$(git rev-parse --abbrev-ref HEAD)
echo ${APP_BRANCH_FROM_GIT}

APP_VERSION_FILE=./${APP}.version
APP_VERSION=${APP_VERSION_FROM_MAVEN}-${APP_BRANCH_FROM_GIT}_${NOW}_${THEUSER}
echo ${APP_VERSION} > ${APP_VERSION_FILE}

echo "LOCALLY building runtime to local folder: ./build ..."
echo "Version = ${APP_VERSION}"

if [ ! -f "${APP_VERSION_FILE}" ]; then
    echo "APP Version file DOES NOT exist. CANNOT proceed with build."
    exit 1
fi

# APP Java
if [ ! -f "${APP_VERSION_FILE}" ]; then
    echo "APP Version file DOES NOT exist. CANNOT proceed with build."
    exit 1
fi

#
# Config
#
APP_PROP_FILE_NAME=${APP}.properties
APP_PROP_FILE=./opt/${APP_PROP_FILE_NAME}
APP_TEMPLATE_PROP_FILE=./opt/${APP}.properties-template

[ -e ${APP_PROP_FILE} ] && echo "Deleting ${APP_PROP_FILE} ..." && /bin/rm -f ${APP_PROP_FILE} && echo "Done."
cat ${APP_TEMPLATE_PROP_FILE} | sed -e "s#THEVERSION#${APP_VERSION}#g" > ${APP_PROP_FILE}

#
# Build
#
echo "Building application ${APP} with version ${APP_VERSION}..."
${MVN_CMD} install -Dmaven.test.skip=true
${MVN_CMD} package -N -Dmaven.test.skip=true

#
# Deploy to ./build
#
LOCAL_TARGET_ROOT=./build
LOCAL_TARGET_DIR=${LOCAL_TARGET_ROOT}/${APP}

DEPENDENT_JARS_PREFIX=./target/classpathDependencies
TARGET_FILE_JAR_FILE=${APP}-${APP_VERSION_FROM_MAVEN}.jar
TARGET_FILE_JAR=./target/${TARGET_FILE_JAR_FILE}
TARGET_FILE_LOG4J=./opt/log4j.properties
# place spaces between jar files, e.g., "z.jar x.jar y.jar"
TARGET_FILES_SH="./opt/run-${APP}.sh"
# place spaces between jar files, e.g., "z.jar x.jar y.jar"
DEPENDENT_JARS=$(ls ${DEPENDENT_JARS_PREFIX})

echo "Deploying locally to \"${LOCAL_TARGET_DIR}\"";
mkdir -p ${LOCAL_TARGET_DIR}/jars
cp ${TARGET_FILE_JAR} ${LOCAL_TARGET_DIR}/jars
cp ${TARGET_FILE_LOG4J} ${LOCAL_TARGET_DIR}
cp ${APP_PROP_FILE} ${LOCAL_TARGET_DIR}
CLASSPATH="./:./jars/${TARGET_FILE_JAR_FILE}"
for jar in ${DEPENDENT_JARS}
do
  cp ${DEPENDENT_JARS_PREFIX}/${jar} ${LOCAL_TARGET_DIR}/jars
  CLASSPATH=${CLASSPATH}:./jars/${jar}
done
for driver in ${TARGET_FILES_SH}
do
  cp ${driver} ${LOCAL_TARGET_DIR}
done

SIMPLE_DRIVER=${LOCAL_TARGET_DIR}/run.sh

cat << EOF > ${SIMPLE_DRIVER}

EOF
chmod +x ${SIMPLE_DRIVER}

echo "CLASSPATH=$CLASSPATH"
echo "java -Dlog4j.configuration=file:./log4j.properties edu.cooper.ece465.apps.imaging.ImagingService"

#
# Deploy
#

echo "Done building ${APP}."
