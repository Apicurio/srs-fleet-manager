#! /bin/bash
set -euxo pipefail

(kubectl port-forward service/apicurio-registry-mt-ui 9090:8080) & PID_PF1=$!
(kubectl port-forward service/fleet-manager 8081:8081) & PID_PF2=$!
(kubectl port-forward service/registry-app 8082:8080) & PID_PF3=$!
(kubectl port-forward service/keycloak 8083:8080) & PID_PF4=$!

trap "kill -9 $PID_PF1 && kill -9 $PID_PF2 && kill -9 $PID_PF3 && kill -9 $PID_PF4" SIGTERM SIGINT

wait $PID_PF1 $PID_PF2 $PID_PF3 $PID_PF4
