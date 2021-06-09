

TENANT_MANAGER_URL=http://$(oc get route tenant-manager --template='{{ .spec.host }}')
REGISTRY_URL=http://$(oc get route apicurio-registry --template='{{ .spec.host }}')

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/serviceregistry_mgmt/v1/admin/registryDeployments tenantManagerUrl=$TENANT_MANAGER_URL registryDeploymentUrl=$REGISTRY_URL

echo 
sleep 1

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/serviceregistry_mgmt/v1/admin/registryDeployments