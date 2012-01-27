#!/bin/bash
#set -xv

clear

mvn clean 
mvn compile
mvn test-compile
mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignPushClientExample -Dexec.classpathScope=test $1

echo "Done"
