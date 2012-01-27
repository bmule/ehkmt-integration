#!/bin/bash
#set -xv

# this bash script is used to pack and deploy the phrweb project in to an
# existing Tomcat instance. 

clear

#not needed with groovy compile ./package.bash

tomcat_home="/lab0/apache/tomcat/apache-tomcat-6.0.35/"
# tomcat_home="/Users/bmulreni/development/development-tools/tomcat6"
mvn clean 
mvn compile
mvn  cargo:deploy  -Dcatalina.home=$tomcat_home

echo ""
echo "The phrweb is deployed on :"$tomcat_home
echo ""
