#!/bin/bash
#set -xv

# $1 is the path to the directory used like workspace

if [ ! -d $1 ]; then
    mkdir -p $1
fi


mvn eclipse:configure-workspace -Declipse.workspace=$1 