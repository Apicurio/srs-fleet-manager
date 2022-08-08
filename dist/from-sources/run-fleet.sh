#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGET="${1:-${SCRIPT_DIR}/target}"

if [[ -z "${TENANT_MANAGER_CLIENT_SECRET}" ]]; then
  echo "Please specify the 'TENANT_MANAGER_CLIENT_SECRET' env variable"
  exit 1
fi

export USE_LOCAL_AMS=true
export AUTH_ENABLED=true
export KEYCLOAK_URL='https://auth.apicur.io/auth'
export KEYCLOAK_REALM='operate-first-apicurio'
export TENANT_MANAGER_AUTH_ENABLED='true'
export TENANT_MANAGER_AUTH_SERVER_URL='https://auth.apicur.io/auth'
export TENANT_MANAGER_AUTH_SERVER_REALM='operate-first-apicurio'
export TENANT_MANAGER_AUTH_CLIENT_ID='sr-tenant-manager'
export TENANT_MANAGER_AUTH_SECRET="${TENANT_MANAGER_CLIENT_SECRET}"
export ORGANIZATION_ID_CLAIM="organization_id"

export REGISTRY_QUOTA_PLANS_CONFIG_FILE="${TARGET}/srs-fleet-manager/dist/docker-compose/config/quota-plans.yaml"
export REGISTRY_DEPLOYMENTS_CONFIG_FILE="${SCRIPT_DIR}/registry-deployments.yaml"

export KEYCLOAK_API_CLIENT_ID='sr-fleet-manager'

mvn compile exec:java -Dexec.mainClass="org.bf2.srs.fleetmanager.FleetManagerQuarkusMain" -f ${TARGET}/srs-fleet-manager/core/pom.xml & child=$!

trap "kill -9 "$child"" SIGTERM SIGINT
wait "$child"
