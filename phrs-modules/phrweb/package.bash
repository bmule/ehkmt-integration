#!/bin/bash
#set -xv

# this bash script is used to pack all the phrweb project in to an
# singular war file. The location of the file is target/ directory. 

clear

mvn clean

mkdir   target

tar  zxf src/main/assembly/classes.tgz
mv classes  target/classes/

mvn package -DskipTests=true


echo ""
echo "The phrs war file is ready."
echo ""
