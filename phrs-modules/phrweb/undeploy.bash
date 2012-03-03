#!/bin/bash
#set -xv

clear

#tomcat_home="/lab0/apache/tomcat/apache-tomcat-6.0.35/"
tomcat_home="/Users/bmulreni/development/development-tools/tomcat6/"

mvn  cargo:undeploy  -Dcatalina.home=$tomcat_home

echo ""
echo "The phrweb is undeployed(removed) on web comtainer located here :"$tomcat_home
echo ""
