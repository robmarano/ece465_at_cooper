#!/usr/bin/env bash

CLASSPATH=~/dev/cooper/ece465/ece465_at_cooper/apps/imaging/target/imaging-1.0.0-jar-with-dependencies.jar
java -cp $CLASSPATH edu.cooper.ece465.apps.imaging.ImagingNode --id NODE1 --port 5000 --peer localhost --peerport  5001 --file node1.cmd
