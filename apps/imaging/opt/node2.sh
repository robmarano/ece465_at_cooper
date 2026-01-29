#!/usr/bin/env bash

NODE_ID=node-2
NODE_VERSION=1.0.0
PORT=5001
PEER_HOST=localhost
PEER_PORT=5000
APP_HOME=~/dev/cooper/ece465/ece465_at_cooper/apps/imaging/build/imaging/node2
LOGS_HOME=${APP_HOME}/logs
mkdir -p ${LOGS_HOME}
CMD_FILE=${APP_HOME}/node2.cmd
CLASSPATH=${APP_HOME}/imaging-${NODE_VERSION}-jar-with-dependencies.jar
cd ${APP_HOME}
echo "Starting up $NODE_ID on port $PORT and waiting to connect to $PEER_HOST:$PEER_PORT..."
java -cp $CLASSPATH edu.cooper.ece465.apps.imaging.ImagingNode --id ${NODE_ID} --port ${PORT} --peer ${PEER_HOST} --peerport ${PEER_PORT} --file ${CMD_FILE} > ${LOGS_HOME}/${NODE_ID}.log 2>&1 &
echo "Done. See ${LOGS_HOME}/${NODE_ID}.log for log."
cd -
