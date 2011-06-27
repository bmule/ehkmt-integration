#!/bin/sh
#set -xv

# I need to set the header Content-Type to application/json
NAMES="aaa bbb ccc ddd eee "
for i in $NAMES
do
    document='{"name" : "'$i'"}'
    curl -H"Content-Type: application/json" -X POST http://127.0.0.1:5984/phrs_test -d '{"name":"xxx"}'
    echo $document was added
done



