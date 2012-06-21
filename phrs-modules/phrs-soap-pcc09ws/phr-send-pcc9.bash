#!/bin/bash
#set -xv

clear


mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendComplexPcc09MessageExample \
 -Dexec.args="http://localhost:8089/testws/pcc9 http://localhost:8989/testwsXXX/pcc10 COBSCAT 191 Suzie Mayr F"

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendComplexPcc09MessageExample \
 -Dexec.args="http://localhost:8089/testws/pcc9 http://localhost:8989/testwsXXX/pcc10 MEDLIST 191 Suzie Mayr F"

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendComplexPcc09MessageExample \
 -Dexec.args="http://localhost:8089/testws/pcc9 http://localhost:8989/testwsXXX/pcc10 MEDCCAT 191 Suzie Mayr F"