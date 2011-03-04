#!/bin/bash
#set -xv

#cretes the phrs coutch db database
curl -X PUT http://127.0.0.1:5984/phrs_test

#adds the default document
curl -X PUT http://127.0.0.1:5984/phrs_test/_design/phrs_document -d @phrs_document.json