

TENANT_MANAGER_URL=http://$(oc get route tenant-manager --template='{{ .spec.host }}')
REGISTRY_URL=http://$(oc get route apicurio-registry --template='{{ .spec.host }}')

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/v1/admin/registry-deployments tenantManagerUrl=$TENANT_MANAGER_URL registryDeploymentUrl=$REGISTRY_URL

echo 
sleep 1

http http://$(oc get route service-api --template='{{ .spec.host }}')/api/v1/admin/registry-deployments