
export TENANT_MANAGER_URL=http://$(oc get route tenant-manager --template='{{ .spec.host }}')
export REGISTRY_URL=http://$(oc get route apicurio-registry --template='{{ .spec.host }}')

export SERVICE_API_URL=http://$(oc get route service-api --template='{{ .spec.host }}')

export AUTH_SERVER_URL=https://$(oc get route keycloak --template='{{ .spec.host }}')/auth

export ADMIN_USERNAME=$(oc get secret credential-example-keycloak -o json | jq -r .data.ADMIN_USERNAME | base64 -d -)
export ADMIN_PASSWORD=$(oc get secret credential-example-keycloak -o json | jq -r .data.ADMIN_PASSWORD | base64 -d -)

