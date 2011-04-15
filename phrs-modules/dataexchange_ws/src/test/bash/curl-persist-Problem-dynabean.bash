#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# _phrsBeanClassURI = at.srfg.kmt.ehealth.phrs.datamodel.impl.Probmel
# Here I send to the backend a ActivityItem in JSON form to be persisted
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ObsIssue","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.Probmel","_phrsBeanCreateDate":"2011-04-14T15:29:17Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_f2bedbd4-0f1a-41c5-9f89-4a70ca481987","comment":"sadf","isActiveStatus":false,"issueCode":"2.16.840.1.113883.6.96:C1457887","issueTypeCode":"2.16.840.1.113883.6.96:C1457887","observationDateEnd":"2011-04-14T15:28:00Z","observationDateStart":"2011-04-14T15:28:00Z","ownerUser":{"class":"User","id":2}}' $HOST/$CONTEXT/persist
 