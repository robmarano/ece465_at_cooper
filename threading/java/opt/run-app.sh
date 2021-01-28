#!/bin/bash
### BEGIN INIT INFO
# Provides:	run-app
# Required-Start:    $local_fs $syslog $network
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
### END INIT INFO

# How to run on host
# $ sudo ./run-${APP_NAME}.sh start

NOW=$(date "+%Y%m%d%H%M%S")

# Clear out old dependencies
###/bin/rm -rf $HOME/.ivy2
###/bin/rm -rf $HOME/.m2

# Setting up environment for TMLA EMR Spark Cluster
export APP_DESCR="Single Node Multi-Threaded App"
export APP_NAME=single_node_threaded_app
export PIDFILE=${APP_NAME}.pid
export APP_USER=${USER}

export APP_RUNTIME_PATH=$(/bin/pwd)
export VERSION="0.1.1"

export APP_EXECUTABLE_JAR="${APP_NAME}-${APP_VERSION}.jar"
export APP_JAR_PATH="${APP_RUNTIME_PATH}/${APP_EXECUTABLE_JAR}"
export APP_PROPS_FILE="${APP_RUNTIME_PATH}/${APP_NAME}.properties"
export APP_LOG4J="log4j.properties"
export APP_LOG4J_FILE="${APP_RUNTIME_PATH}/${APP_LOG4J}"
export APP_LOG_FILES="${APP_RUNTIME_PATH}/${APP_NAME}.log"
export APP_CLASSNAME=edu.cooper.ece465.ProducerConsumerTest

export RUNTIME_JARS="${APP_RUNTIME_PATH}/commons-${APP_VERSION}.jar" # common delimited
export CLASSPATH=$(echo ${RUNTIME_JARS} | sed 's/\,/\:/g')
export CLASSPATH="${APP_JAR_PATH}:${CLASSPATH}"
export NEEDED_EXEC_EXTRA_CLASSPATH="/usr/share/aws/aws-java-sdk/*"
export NEEDED_DRIV_EXTRA_CLASSPATH=${NEEDED_EXEC_EXTRA_CLASSPATH}

export JAVA_OPTS="${JAVA_OPTS} #-Djava.library.path=/usr/lib/hadoop-lzo/lib/native"
export APP_JAVA_OPTS="-Dlog4j.configuration=file://${APP_LOG4J_FILE}"

# For Remote Debugging
#export JAVA_OPTS="${JAVA_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7777"

# Delete log files
# See http://www.bigendiandata.com/2016-08-26-How-to-debug-remote-spark-jobs-with-IntelliJ/
for logfile in ${APP_LOG_FILES}
do
  [ -z "${logfile}" ] && /bin/rm -rf ${logfile};
done

start() {
#  if [ $user_ != $APP_USER ]; then
#    echo "Error: Needs to be ${APP_USER}"
#    exit -1;
#  fi

  echo -n "Starting ${APP_NAME}: "
  cd ${APP_RUNTIME_PATH} && \

  # Start Spark Job
  nohup ${JAVA_RUN} ${APP_JAR_PATH} > /dev/null 2>&1 &


  es=$?
  if [ $es -eq 0 ]; then
    echo " done."
    exit 0
  else
    echo " failed."
    exit $es
  fi
  exit 0
}

getpid() {
  # Get the PID from PIDFILE if we don't have one yet.
  if [[ -z "${PID}" && -e ${PIDFILE} ]]; then
    PID=$(cat ${PIDFILE});
  fi
}

removePidFile() {
  # Get the PID from PIDFILE if we don't have one yet.
  if [[  -e ${PIDFILE} ]]; then
    rm ${PIDFILE};
  fi
}

stop() {
  echo -n "Stopping ${APP_NAME}: "
  getpid
  kill -9 ${PID}
  removePidFile

  es=$?
  if [ $es -eq 0 ]; then
    echo " done."
    exit 0
  else
    echo " failed."
    exit $es
  fi
  exit 0
}

status() {
  getpid
  c_pid=`ps -ef | grep ${PID} | grep -v grep | awk '{print $2}'`
  if [ "$c_pid" = "" ] ; then
    echo "Stopped"
    exit 3
  else
    echo "Running $c_pid"
    exit 0
  fi
  exit 0
}

case "$1" in
  start)
    start
    ;;

  stop)
    stop
    ;;

  hardstop)
    hardstop
    ;;

  restart)
    stop
    start
    ;;

  status)
    status
    ;;

  *)
    echo "Usage: run-${TMLA_APP_NAME}.sh {start|stop|status|restart}"
    exit 1
    ;;

esac

exit 0
