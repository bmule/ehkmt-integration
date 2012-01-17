#!/bin/bash
#set -xv

clear

mvn clean compile
mvn test-compile
# mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="127.0.0.1 8089 testws/pcc9 local.ks simulator"
mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8089 testws/pcc9 srfg-phrs-core-keystore.ks icardea"


