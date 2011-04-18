#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# phrsBeanClassURI : at.srfg.kmt.ehealth.phrs.datamodel.impl.Medication
curl --fail -X POST --data 'dynaBean={"class":"at.srfg.kmt.ehealth.phrs.presentation.model.patientdata.MedicationSummary","id":null,"_phrsBeanCanRead":true,"_phrsBeanCanUse":true,"_phrsBeanCanWrite":true,"_phrsBeanClassURI":"at.srfg.kmt.ehealth.phrs.datamodel.impl.Medication","_phrsBeanCreateDate":"2011-04-14T15:25:05Z","_phrsBeanCreatorUri":"at.srfg.kmt.ehealth.phrs.datamodel.impl.Medication","_phrsBeanIsDeleted":false,"_phrsBeanName":null,"_phrsBeanOwnerUri":null,"_phrsBeanRefersToSourceUri":null,"_phrsBeanUri":"phrs_uuid_5fa83360-cfef-4c50-837d-6c3c81c60d85","comment":null,"medicationActivity":null,"medicationCode":null,"medicationFrequencyInterval":"phf:hourly","medicationFrequencyQuantity":"phf:3","medicationFrequencyTimeOfDay":"phf:morning","medicationNameText":"the med","medicationQuantity":"2","medicationQuantityUnit":"phf:milligram","medicationReasonComment":null,"medicationReasonKeywordCodes":null,"medicationReasonPrimaryKeywordCode":"phf:cholesterol","medicationStatus":"phf:active","observationDateEnd":null,"observationDateStart":"2011-04-14T15:23:00Z","ownerUser":{"class":"User","id":2},"prescribedByPersonId":null,"prescribedByPersonName":"dr jones","prescribedByPersonRole":""}' $HOST/$CONTEXT/push
 