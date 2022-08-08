#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGET="${1:-${SCRIPT_DIR}/target}"

if [[ -z "${TENANT_MANAGER_CLIENT_SECRET}" ]]; then
  echo "Please specify the 'TENANT_MANAGER_CLIENT_SECRET' env variable"
  exit 1
fi

export AUTH_ENABLED=true
export KEYCLOAK_URL='https://auth.apicur.io/auth'
export KEYCLOAK_REALM='operate-first-apicurio'
export REGISTRY_ROUTE_URL='http://localhost:8080'

export KEYCLOAK_API_CLIENT_ID='sr-tenant-manager'

mvn compile exec:java -Dexec.mainClass="io.apicurio.multitenant.api.TenantManagerQuarkusMain" -f ${TARGET}/apicurio-registry/multitenancy/tenant-manager-api/pom.xml & child=$!

trap "kill -9 "$child"" SIGTERM SIGINT
wait "$child"

