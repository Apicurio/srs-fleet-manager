TENANT_MANAGER_URL=$1
REGISTRY_URL=$2

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/v1/admin/registry-deployments tenantManagerUrl=$TENANT_MANAGER_URL registryDeploymentUrl=$REGISTRY_URL

echo 
sleep 1

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/v1/admin/registry-deployments