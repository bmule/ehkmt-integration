#!/bin/bash
#set -xv


#$1 - the application to depoy

$GERONIMO_HOME/bin/deploy --user system --password manager deploy --inPlace $1
