#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGET="${1:-${SCRIPT_DIR}/target}"

if [[ -z "${TENANT_MANAGER_CLIENT_SECRET}" ]]; then
  echo "Please specify the 'TENANT_MANAGER_CLIENT_SECRET' env variable"
  exit 1
fi

export REGISTRY_ENABLE_MULTITENANCY=true
export AUTH_ENABLED=true
export KEYCLOAK_URL='https://auth.apicur.io/auth'
export KEYCLOAK_REALM='operate-first-apicurio'
export TENANT_MANAGER_AUTH_ENABLED='true'
export TENANT_MANAGER_AUTH_URL='https://auth.apicur.io/auth'
export TENANT_MANAGER_REALM='operate-first-apicurio'
export TENANT_MANAGER_CLIENT_ID='sr-tenant-manager'
export TENANT_MANAGER_CLIENT_SECRET="${TENANT_MANAGER_CLIENT_SECRET}"
export TENANT_MANAGER_URL='http://localhost:8585'
export ORGANIZATION_ID_CLAIM="organization_id"

export KEYCLOAK_API_CLIENT_ID='sr-api'

mvn compile exec:java -Dexec.mainClass="io.apicurio.registry.RegistryQuarkusMain" -f ${TARGET}/apicurio-registry/app/pom.xml & child=$!

trap "kill -9 "$child"" SIGTERM SIGINT
wait "$child"
