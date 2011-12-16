#!/bin/bash
#set -xv

clear

mvn clean

mkdir   target
tar  zxf src/main/assembly/classes.tgz
mv classes  target/classes/

mvn package -DskipTests=true


echo "Package was done"
echo
echo
