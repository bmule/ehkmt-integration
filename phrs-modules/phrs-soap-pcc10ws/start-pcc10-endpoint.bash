#!/bin/bash
#set -xv

clear

mvn clean compile
mvn test-compile
mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10EndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8989 testws/pcc10"


