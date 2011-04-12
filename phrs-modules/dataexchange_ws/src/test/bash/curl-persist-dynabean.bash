#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/dynamic_bean_repository

curl --fail -X POST --data 'dynaBean={"label":"curlTag2"}' $HOST/$CONTEXT/persist