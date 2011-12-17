#!/bin/bash
#set -xv

# this bash script is used to pack and deploy the phrweb project in to an
# existing Tomcat instance. 

clear

./package.bash

tomcat_home="/lab0/apache/tomcat/apache-tomcat-6.0.35/"

mvn  cargo:deploy  -Dcatalina.home=$tomcat_home

echo ""
echo "The phrweb is deployed on :"$tomcat_home
echo ""
