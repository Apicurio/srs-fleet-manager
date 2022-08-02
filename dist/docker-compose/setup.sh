#!/bin/bash

if [ -z "$1" ]
then
  echo "Please provide the necessary arguments!"
  exit 1
fi

HOST_IP=$1
P=$(pwd)

##if the script runs in the container, we have to adjust the path to the mount point
if [ $P == "/" ]
then
  export P=/apicurio
fi

sed 's/$HOST/'"$HOST_IP"'/g' $P/.env.template > $P/tmp; mv $P/tmp $P/.env

echo "Registry URL: $HOST_IP:8080"
echo "Tenant Manager URL: $HOST_IP:8585"
echo "Fleet Manager URL: $HOST_IP:8081"