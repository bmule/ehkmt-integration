#!/bin/bash
#set -xv

clear

./package.bash

tomcat_home="/lab0/apache/tomcat/apache-tomcat-6.0.35/"

#mvn  cargo:undeploy  -Dcatalina.home=$tomcat_home
mvn  cargo:deploy    -Dcatalina.home=$tomcat_home
