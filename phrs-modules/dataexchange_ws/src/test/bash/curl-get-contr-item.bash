#!/bin/bash
#set -xv

HOST=http://localhost:8080
CONTEXT=dataexchange_ws/controlled_item_repository

# this will extracts only one item with a certain code system and code.
# 
#2.16.840.1.113883.6.96:19019007 - the syntax is code system code : item code
curl --fail  -X GET $HOST/$CONTEXT/get?q=2.16.840.1.113883.6.96:C1457887