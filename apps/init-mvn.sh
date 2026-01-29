#!/bin/bash

mvn archetype:generate \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=edu.cooper.ece465.apps \
  -Dname=imaging \
  -DartifactId=imaging \
  -Dversion=1.0.0 \
  -Ddescription="Distributed Imaging Service" \
  -Dshaded=true \
  -DinteractiveMode=false
