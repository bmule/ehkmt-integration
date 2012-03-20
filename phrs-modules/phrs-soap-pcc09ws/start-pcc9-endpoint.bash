#!/bin/bash
#set -xv

clear

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.PCC9EndpointExample  -Dexec.args="localhost 8089 testws/pcc9"
