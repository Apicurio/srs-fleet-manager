
if [ -z "$1" ]
  then
    echo "Control plane tenant id is required"
    exit 1
fi

CONTROL_PLANE_TENANT_ID=$1

curl -k --location --request POST https://$(oc get route keycloak --template='{{ .spec.host }}')/auth/realms/sr-tenant-$CONTROL_PLANE_TENANT_ID/protocol/openid-connect/token \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=apicurio-registry' \
--data-urlencode "username=sr-admin-tenant-$CONTROL_PLANE_TENANT_ID" \
--data-urlencode 'password=password'