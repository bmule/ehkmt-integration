#!/bin/bash
#set -xv

clear


mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.simple.TestEndPoint -Dexec.classpathScope=test


