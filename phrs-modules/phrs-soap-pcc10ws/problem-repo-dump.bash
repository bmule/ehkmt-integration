#!/bin/bash
#set -xv

clear

mvn clean compile
mvn test-compile
mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.ProbelmRepoDumpExample -Dexec.classpathScope=test -Dexec.args="191" -X
