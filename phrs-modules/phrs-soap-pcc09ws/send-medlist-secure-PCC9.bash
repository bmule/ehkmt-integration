#!/bin/bash
#set -xv

clear

mvn compile
mvn test-compile

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="https://localhost:8089/testws/pcc9 https://localhost:8989/testws/pcc10  /Volumes/Data/lab0/iiiiiCardea/phrs/ehkmt-integration/etc/ssl/srfg-phrs-core-keystore.ks icardea COBSCAT 198 Moppuffus Lumpkins M"
# mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendSecurePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="https://localhost:8089/testws/pcc9 https://localhost:8989/testws/pcc10 src/test/resources/local.ks simulator"
