#!/bin/sh
#set -xv

# I need to set the header Content-Type to application/json
curl http://127.0.0.1:5984/phrs_test/_design/phrs_document/_view/

