#!/usr/bin/env bash

TOP_DIR=~/dev/cooper/ece465/ece465_at_cooper/apps/imaging
PORT=1859
CLASSPATH=$TOP_DIR/target/imaging-1.0.0-jar-with-dependencies.jar
java -cp $CLASSPATH -Dlog4j.configuration=file:$TOP_DIR/opt/log4j.properties edu.cooper.ece465.apps.imaging.ImagingService $PORT
