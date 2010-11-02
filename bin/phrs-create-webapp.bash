#!/bin/bash
#set -xv

# don't change this, this the master package for all the PHRS relarted classes
GROUP_ID="at.srfg.kmt.ehealth.phrs"

# name must be java package conform  
ARTIFACT_ID=$1

mvn archetype:create                           \
  -DarchetypeArtifactId=maven-archetype-webapp \
  -DgroupId=$GROUP_ID                          \
  -DartifactId=$ARTIFACT_ID
  