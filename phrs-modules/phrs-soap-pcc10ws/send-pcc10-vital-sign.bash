#!/bin/bash
#set -xv

clear

mvn compile
mvn test-compile

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendVitalSignPCC10Message -Dexec.classpathScope=test -Dexec.args="http://localhost:8989/testws/pcc10"
