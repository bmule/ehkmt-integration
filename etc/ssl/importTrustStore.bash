#!/bin/bash

# $1 the keystore file
# $2 the truststore file

clear
echo "Export certificate from the keystore."

#$1 - the keystore file
 
$JAVA_HOME/bin/keytool -export -alias tomcat -file mykey.cer -rfc -keystore $1
echo "Export certificate from the keystore -> done."

echo "Certificate looks like :" 
cat mykey.cer

echo "Import certificate from the keystore in to truststore."
$JAVA_HOME/bin/keytool -import -alias tomcat -file mykey.cer -keystore $2
echo "Import certificate from the keystore in to truststore -> done."
rm mykey.cer

echo "Truststore looks like : "
$JAVA_HOME/bin/keytool -list -v -keystore $2

