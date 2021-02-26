#!/bin/bash -x
### BEGIN INIT INFO
# Provides:    imaging
# Required-Start:    
# Required-Stop:     
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
### END INIT INFO

export APP_NAME=imaging
export APP="edu.cooper.ece465.apps.imaging.ImagingService"

export APP_USER=ec2-user
export APP_HOME=/opt/${APP_NAME}
#export APP_HOST=172.31.1.60
export APP_HOST=localhost
export APP_PORT=1859
export VERSION="0.0.28-SNAPSHOT"
export EXECUTABLE="${APP_HOME}/${APP_NAME}-${VERSION}-jar-with-dependencies.jar"
export APP_CLASSPATH=${EXECUTABLE}

#export JAVA_HOME=/usr/java/latest
#export JAVA_OPTS="-server -Djava.awt.headless=true -Xms384M -Xmx512M -XX:MaxPermSize=256M"
#export HEAP_DUMP_FILE=${APP_HOME}/app-${NOW}.dump.hprof
######export JAVA_OPTS="-server -Xms500m -Xmx1g -XX:MaxMetaspaceSize=128m -XX:+UseParallelGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${HEAP_DUMP_FILE}"
##export LOG_LEVEL=DEBUG
##export LOG4J_FILE=${APP_HOME}/log4j.properties
###export CLASSPATH=${EXECUTABLE}:.
export CLASSPATH=${EXECUTABLE}

case "$1" in

  start)
    echo -n "Starting imaging:"
/sbin/runuser ${APP_USER} -c "cd ${APP_HOME && \
  nohup java -cp ${APP_CLASSPATH} -DAPP_HOST=${APP_HOST} -DAPP_PORT=${APP_PORT} ${APP} &"

    echo " done."
    exit 0
    ;;

  stop)
    echo -n "Stopping totem-os: "
    /sbin/runuser ${APP_USER} -c "ps -ef | grep ${APP} | grep -v grep | awk '{print \$2}' | sudo xargs kill"
    echo " done."
    exit 0
    ;;

  hardstop)
    echo -n "Stopping (hard) imaging: "
    /sbin/runuser ${APP_USER} -c "ps -ef | grep ${APP} | grep -v grep | awk '{print \$2}' | sudo xargs kill -9"
    echo " done."
    exit 0
    ;;

  restart)
    stop
    start
    ;;

  status)
    c_pid=`ps -ef | grep ${APP} | grep -v grep | awk '{print $2}'`
    if [ "$c_pid" = "" ] ; then
      echo "Stopped"
      exit 3
    else
      echo "Running $c_pid"
      exit 0
    fi
    ;;

  *)
    echo "Usage: run-${APP_NAME}.sh {start|stop|hardstop|status|restart}"
    exit 1
    ;;

esac
