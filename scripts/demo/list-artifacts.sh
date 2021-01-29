
if [ -z "$1" ]
  then
    echo "Control plane tenant id is required"
    exit 1
fi

CONTROL_PLANE_TENANT_ID=$1

BEARER_TOKEN=$(curl -k --location --request POST https://$(oc get route keycloak --template='{{ .spec.host }}')/auth/realms/sr-tenant-$CONTROL_PLANE_TENANT_ID/protocol/openid-connect/token \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=apicurio-registry' \
--data-urlencode "username=sr-admin-tenant-$CONTROL_PLANE_TENANT_ID" \
--data-urlencode 'password=password' | jq -r .access_token )

TENANT_URL=$(http http://$(oc get route service-api --template='{{ .spec.host }}')/api/v1/registries/$CONTROL_PLANE_TENANT_ID | jq -r .appUrl)

curl -v --location -i $TENANT_URL/api/artifacts --header "Authorization: Bearer $BEARER_TOKEN" \