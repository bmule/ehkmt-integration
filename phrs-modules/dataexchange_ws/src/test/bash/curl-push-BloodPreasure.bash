#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ObsBloodPressureA01","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure","_phrsBeanCreateDate":"2011-04-14T15:30:56Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_b5f9c623-65af-4db8-a61d-ef5b9b7b4edb","bpDiastolic":78,"bpHeartRate":78,"bpSystolic":123,"comment":"sf","observationDate":"2011-04-14T15:30:00Z","ownerUser":{"class":"User","id":2}}' $HOST/$CONTEXT/persist
 