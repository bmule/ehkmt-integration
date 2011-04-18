#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# Here I send to the backend a ActivityItem in JSON form to be persisted
# _phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityOfDailyLiving
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ActivityOfDailyLivingItem","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityOfDailyLiving","_phrsBeanCreateDate":"2011-04-14T15:39:54Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_379b7cbf-340f-454c-b7b3-1a56efb639d0","activityCategory":null,"activityCode":"phf:climbstairs","activityDurationCode":null,"activityFeature":{},"isActivityAssisted":false,"isActivityUnassisted":true,"ownerUser":{"class":"User","id":2},"score":null,"valueActivity":"phf:assistedActivity"}' $HOST/$CONTEXT/push
 