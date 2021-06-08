
FLEET_MANAGER_URL=$1
#just a name to identify it
REGISTRY_NAME=$2

http http://$FLEET_MANAGER_URL/api/v1/registries name=$REGISTRY_NAME

echo 
sleep 1

http http://$FLEET_MANAGER_URL/api/v1/registries