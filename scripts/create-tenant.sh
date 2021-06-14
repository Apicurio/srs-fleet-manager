
FLEET_MANAGER_URL=$1
#just a name to identify it
REGISTRY_NAME=$2

http https://$FLEET_MANAGER_URL/api/serviceregistry_mgmt/v1/registries name=$REGISTRY_NAME

echo 
sleep 1

http https://$FLEET_MANAGER_URL/api/serviceregistry_mgmt/v1/registries