#!/bin/bash
#set -xv

clear

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample  -Dexec.args="localhost 8989 testws/pcc10 srfg-phrs-core-keystore.ks icardea" -Dpcc10.process="true"


