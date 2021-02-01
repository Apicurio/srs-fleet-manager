
AUTH_SERVER_URL=https://$(oc get route keycloak --template='{{ .spec.host }}')/auth

ADMIN_USERNAME=$(oc get secret credential-example-keycloak -o json | jq -r .data.ADMIN_USERNAME | base64 -d -)
ADMIN_PASSWORD=$(oc get secret credential-example-keycloak -o json | jq -r .data.ADMIN_PASSWORD | base64 -d -)

