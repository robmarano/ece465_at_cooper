threadedJavaJourney
===================

Take a trip on the wonderful world of threading in Java

How to run the client/server code in sockets:
mvn exec:exec -Dexec.executable="java" -Dexec.args="-cp %classpath edu.cooper.ece465.sockets.DistributedImagingServer 1971"
mvn exec:exec -Dexec.executable="java" -Dexec.args="-cp %classpath edu.cooper.ece465.sockets.DistributedImagingClient localhost 1971"
