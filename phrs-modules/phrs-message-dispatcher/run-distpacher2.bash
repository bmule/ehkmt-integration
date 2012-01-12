#!/bin/bash
#set -xv

clear

mvn compile
mvn test-compile

mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dispatch.impl.RunSecondDispatcher -Dexec.classpathScope=test
