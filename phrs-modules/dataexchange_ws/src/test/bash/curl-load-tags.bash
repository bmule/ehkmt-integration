#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/controlled_item_repository

#curl --fail  -X GET $HOST/$CONTEXT/get?q=2.16.840.1.113883.6.96:19019007
curl --fail  -X GET $HOST/$CONTEXT/load