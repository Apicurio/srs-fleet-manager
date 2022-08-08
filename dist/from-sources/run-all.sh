#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGET="${1:-${SCRIPT_DIR}/target}"

if [[ -z "${TENANT_MANAGER_CLIENT_SECRET}" ]]; then
  echo "Please specify the 'TENANT_MANAGER_CLIENT_SECRET' env variable"
  exit 1
fi

(${SCRIPT_DIR}/run-registry.sh ${TARGET}) & PID_JOB1=$!
(${SCRIPT_DIR}/run-tenant.sh ${TARGET}) & PID_JOB2=$!
(${SCRIPT_DIR}/run-fleet.sh ${TARGET}) & PID_JOB3=$!
(${SCRIPT_DIR}/run-registry-ui.sh ${TARGET}) & PID_JOB4=$!

trap "kill $PID_JOB1 && kill $PID_JOB2 && kill $PID_JOB3 && kill $PID_JOB4" SIGTERM SIGINT

wait $PID_JOB1 $PID_JOB2 $PID_JOB3 $PID_JOB4
