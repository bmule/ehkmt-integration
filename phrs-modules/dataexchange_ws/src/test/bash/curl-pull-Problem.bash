#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.Probmel
curl --fail  -X GET $HOST/$CONTEXT/getLastForClass?class_uri=at.srfg.kmt.ehealth.phrs.datamodel.impl.Probmel
 