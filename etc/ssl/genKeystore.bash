#!/bin/bash
#set -xv

$JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA -keystore $1
$JAVA_HOME/bin/keytool -list -v -keystore $1

		
