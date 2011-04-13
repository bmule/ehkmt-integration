#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

# this is the BodyWeight
curl --fail -X POST --data 'dynaBean={"class_uri":"at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight", "class":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ObsBodyWeightBMW01", "id":null, "bodyBMI":null, "bodyweight":70.0, "comment":"comments are here", "height":174.0, "isDeleted":false, "msystem":"metric", "observationDate":"2011-04-12T14:32:00Z",     "ownerUser":{"class":"User","id":2}, "phrs_createDate":"2011-04-12T14:32:35Z", "phrs_creatorUri":"phrsuid_e4c6006d-8e7c-4951-8aae-1cabe5d8d280",  "phrs_displayName":null, "phrs_ownerUri":"phrsuid_e4c6006d-8e7c-4951-8aae-1cabe5d8d280",  "phrs_refersToSourceUri":null,  "phrs_typePrimary":"at.srfg.kmt.ehealth.phrs.presentation.model.observation.ObsBodyWeightBMW01", "phrs_uri":"phrs_uuid_2b1c54d9-ee75-47ef-94de-8e6fbc9158bb"}' $HOST/$CONTEXT/persist
 