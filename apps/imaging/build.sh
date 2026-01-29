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
cp ${APP_PROP_FILE} ./src/main/resources

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
LOCAL_TARGET_DIR=${LOCAL_TARGET_ROOT}
APP_JAR=./target/imaging-${APP_VERSION_FROM_MAVEN}-jar-with-dependencies.jar
DEPENDENT_JARS_PREFIX=./target/classpathDependencies
TARGET_FILE_JAR_FILE=${APP}-${APP_VERSION_FROM_MAVEN}.jar
TARGET_FILE_JAR=./target/${TARGET_FILE_JAR_FILE}
TARGET_FILE_LOG4J=./opt/log4j.properties
# place spaces between jar files, e.g., "z.jar x.jar y.jar"
TARGET_FILES_SH="./opt/run-${APP}.sh"
# place spaces between jar files, e.g., "z.jar x.jar y.jar"
DEPENDENT_JARS=$(ls ${DEPENDENT_JARS_PREFIX})

echo "Deploying locally to \"${LOCAL_TARGET_DIR}\"";
mkdir -p ${LOCAL_TARGET_DIR}/node1/jars
mkdir -p ${LOCAL_TARGET_DIR}/node2/jars
cp ${TARGET_FILE_JAR} ${LOCAL_TARGET_DIR}/node1/jars
cp ${TARGET_FILE_JAR} ${LOCAL_TARGET_DIR}/node2/jars
cp ${TARGET_FILE_LOG4J} ${LOCAL_TARGET_DIR}/node1
cp ${TARGET_FILE_LOG4J} ${LOCAL_TARGET_DIR}/node2
cp ${APP_PROP_FILE} ${LOCAL_TARGET_DIR}/node1
cp ${APP_PROP_FILE} ${LOCAL_TARGET_DIR}/node2
CLASSPATH="./:./jars/${TARGET_FILE_JAR_FILE}"
for jar in ${DEPENDENT_JARS}
do
  cp ${DEPENDENT_JARS_PREFIX}/${jar} ${LOCAL_TARGET_DIR}/node1/jars
  cp ${DEPENDENT_JARS_PREFIX}/${jar} ${LOCAL_TARGET_DIR}/node2/jars
  CLASSPATH=${CLASSPATH}:./jars/${jar}
done
for driver in ${TARGET_FILES_SH}
do
  cp ${driver} ${LOCAL_TARGET_DIR}/node1
done

cp ./opt/cooper.jpg ${LOCAL_TARGET_DIR}/node1
cp ./opt/node1.cmd ${LOCAL_TARGET_DIR}/node1
cp ${APP_JAR} ${LOCAL_TARGET_DIR}/node1
cp ./opt/test.file ${LOCAL_TARGET_DIR}/node2
cp ./opt/node2.cmd ${LOCAL_TARGET_DIR}/node2
cp ${APP_JAR} ${LOCAL_TARGET_DIR}/node2

NODE1=${LOCAL_TARGET_DIR}/node1.sh

cat << EOF > ${NODE1}
#!/usr/bin/env bash

#!/usr/bin/env bash

NODE_ID=node-1
NODE_VERSION=1.0.0
PORT=5000
PEER_HOST=localhost
PEER_PORT=5001
APP_HOME=~/dev/cooper/ece465/ece465_at_cooper/apps/imaging/build/node1
LOGS_HOME=\${APP_HOME}/logs
mkdir -p \${LOGS_HOME}
CMD_FILE=\${APP_HOME}/node1.cmd
CLASSPATH=\${APP_HOME}/imaging-\${NODE_VERSION}-jar-with-dependencies.jar
cd \${APP_HOME}
echo "Starting up \$NODE_ID on port \$PORT and waiting to connect to \$PEER_HOST:\$PEER_PORT..."
java -cp \$CLASSPATH edu.cooper.ece465.apps.imaging.ImagingNode --id \${NODE_ID} --port \${PORT} --peer \${PEER_HOST} --peerport \${PEER_PORT} --file \${CMD_FILE} > \${LOGS_HOME}/\${NODE_ID}.log 2>&1 &
echo "Done. See \${LOGS_HOME}/\${NODE_ID}.log for log."
cd -


EOF
chmod +x ${NODE1}

NODE2=${LOCAL_TARGET_DIR}/node2.sh

cat << EOF > ${NODE2}
#!/usr/bin/env bash

NODE_ID=node-2
NODE_VERSION=1.0.0
PORT=5001
PEER_HOST=localhost
PEER_PORT=5000
APP_HOME=~/dev/cooper/ece465/ece465_at_cooper/apps/imaging/build/node2
LOGS_HOME=\${APP_HOME}/logs
mkdir -p \${LOGS_HOME}
CMD_FILE=\${APP_HOME}/node2.cmd
CLASSPATH=\${APP_HOME}/imaging-\${NODE_VERSION}-jar-with-dependencies.jar
cd \${APP_HOME}
echo "Starting up \$NODE_ID on port \$PORT and waiting to connect to \$PEER_HOST:\$PEER_PORT..."
java -cp \$CLASSPATH edu.cooper.ece465.apps.imaging.ImagingNode --id \${NODE_ID} --port \${PORT} --peer \${PEER_HOST} --peerport \${PEER_PORT} --file \${CMD_FILE} > \${LOGS_HOME}/\${NODE_ID}.log 2>&1 &
echo "Done. See \${LOGS_HOME}/\${NODE_ID}.log for log."
cd -
EOF
chmod +x ${NODE2}

#
# Deploy
#

echo "Done building ${APP}."
