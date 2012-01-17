#!/bin/bash
#set -xv

alias_name="phrs_core"
keystore_filename="srfg-phrs-core-keystore.ks"
certificate_filename="srfg-phrs-corecertificate.csr"

$JAVA_HOME/bin/keytool -import -alias $alias_name -keystore $keystore_filename -file $certificate_filename

echo "The Certificate $certificate_filename based on the keystore file $keystore_filename was successfuly imported in to the local trust store."

# echo "Actaul certificates lists"
# $JAVA_HOME/bin/keytool -list -v -keystore srfg-phrs-core-keystore.ks	
# $JAVA_HOME/bin/keytool -list -v -keystore $JAVA_HOME/lib/security/cacerts
