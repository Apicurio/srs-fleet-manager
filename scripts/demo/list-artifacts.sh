
CONTROL_PLANE_TENANT_ID=$1

BEARER_TOKEN=$(curl -k --location --request POST https://$(oc get route keycloak --template='{{ .spec.host }}')/auth/realms/sr-tenant-$CONTROL_PLANE_TENANT_ID/protocol/openid-connect/token \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=apicurio-registry' \
--data-urlencode "username=sr-admin-tenant-$CONTROL_PLANE_TENANT_ID" \
--data-urlencode 'password=password' | jq -r .access_token )

TENANT_ID=$2

curl -v --location -i http://$(oc get route apicurio-registry --template='{{ .spec.host }}')/api/artifacts \
    --header "Authorization: Bearer $BEARER_TOKEN" \
    --header "X-Registry-Tenant-Id: $TENANT_ID" 

