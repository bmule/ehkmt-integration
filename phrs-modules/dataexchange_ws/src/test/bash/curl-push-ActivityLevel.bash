#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# _phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityLevel
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ActivityLevel","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityLevel","_phrsBeanCreateDate":"2011-04-14T15:37:50Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_bc4e6344-0a8e-453f-98ac-4467192bb89e","activityLevelIndicator":0,"activityMoodIndicator":0,"comment":null,"observationDate":"2011-04-14T15:37:50Z","ownerUser":{"class":"User","id":2}}' $HOST/$CONTEXT/persist
 