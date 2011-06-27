#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# this is the here all the versions for a a bean with class ...BodyWeight are 
# pulled from the repository.
curl --fail  -X GET $HOST/$CONTEXT/getAllForClass?class_uri=at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight
 