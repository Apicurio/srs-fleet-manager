
FLEET_MANAGER_URL=$1
#tenant manager url can be internal (a.k.a kubernetes service) ie: http://tenant-manager:8585
TENANT_MANAGER_URL=$2
#registry url have to be externally accessible url
REGISTRY_URL=$3
#just a name to easily identify the deployment
DEPLOYMENT_NAME=$4

http http://$FLEET_MANAGER_URL/api/v1/admin/registryDeployments tenantManagerUrl=$TENANT_MANAGER_URL registryDeploymentUrl=$REGISTRY_URL name=DEPLOYMENT_NAME

echo 
sleep 1

http http://$FLEET_MANAGER_URL/api/v1/admin/registryDeployments