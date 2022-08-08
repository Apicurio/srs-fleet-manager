#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGET="${1:-${SCRIPT_DIR}/target}"

yarn --cwd ${TARGET}/apicurio-registry/ui start --no-open & child=$!

trap "kill -9 "$child"" SIGTERM SIGINT
wait "$child"
