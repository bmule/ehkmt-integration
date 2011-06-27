#!/bin/bash

# this script must run in the directory that contains the pom file.


mvn compiler:testCompile -DskipTests=true;mvn exec:java -Dexec.mainClass="at.srfg.kmt.ehealth.phrs.dataexchange.impl.DymanicClassRepositoryBeanExample"  -Dexec.classpathScope=test