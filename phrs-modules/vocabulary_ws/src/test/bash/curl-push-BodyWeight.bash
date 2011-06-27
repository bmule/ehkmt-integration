#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ObsBodyWeightBMW01","class_uri":"at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight","_phrsBeanCreateDate":"2011-04-14T15:58:39Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_f8fee738-679a-4307-b22b-0f8732d35733","bodyBMI":null,"bodyweight":88,"comment":null,"height":162,"msystem":"metric","observationDate":"2011-04-14T15:58:00Z","ownerUser":{"class":"User","id":2}}' $HOST/$CONTEXT/push
 