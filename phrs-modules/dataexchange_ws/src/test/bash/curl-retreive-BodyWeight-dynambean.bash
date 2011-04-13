#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# this is the BodyWeight
curl --fail  -X GET $HOST/$CONTEXT/getLastForClass?class_uri=at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight
 