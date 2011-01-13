#!/bin/bash
#set -xv

# the postgres super user - you may change it according with your needs. 
POSTGRES_SUPER_USER=postgres

dropdb -h localhost -p 5432 phrs_storage  -U $POSTGRES_SUPER_USER
echo "The phrs_storage db was droped"
createdb -h localhost -p 5432 -U $POSTGRES_SUPER_USER -O phrs phrs_storage
echo "The phrs_storage db new created"

