#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.Risk
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.riskfactors.Riskfactor","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.Risk","_phrsBeanCreateDate":"2011-04-14T15:41:02Z","_phrsBeanCreatorUri":null,"_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_898d478a-362e-4e81-9507-e3912cbac031","comment":null,"contributingFactorCodes":[],"hasContributingFactors":false,"isActiveStatus":true,"isTreated":true,"observationDateEnd":"2011-04-14T15:40:00Z","observationDateStart":"2011-04-14T15:40:00Z","ownerUser":{"class":"User","id":2},"riskFactorAttributes":{},"riskFactorCode":"phf:cholesterol","riskFactorDuration":null,"riskFactorType":null,"treatmentStatementPrimary":"true","treatmentStatmentCodes":[]}' $HOST/$CONTEXT/persist
 