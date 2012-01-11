#!/bin/bash
#set -xv

clear

mvn compile
mvn test-compile

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSimplePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="http://localhost:8989/testws/pcc9 http://localhost:8989/testws/pcc10"
