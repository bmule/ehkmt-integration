#!/bin/bash
#set -xv

# $1 is the path to the directory used like workspace

if [ ! -d $1 ]; then
    mkdir -p $1
fi

mvn -Declipse.workspace=$1 eclipse:add-maven-repo

mvn eclipse:eclipse -e
