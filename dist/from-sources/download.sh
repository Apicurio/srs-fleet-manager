#! /bin/bash
set -euxo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

BRANCH="${1:-feat/hackathon}"
TARGET="${2:-${SCRIPT_DIR}/target}"

mkdir -p ${TARGET}

# Clone the repos
git clone https://github.com/Apicurio/apicurio-registry.git --depth 1 --single-branch --branch ${BRANCH} ${TARGET}/apicurio-registry
git clone https://github.com/bf2fc6cc711aee1a0c2a/srs-fleet-manager.git --depth 1 --single-branch --branch ${BRANCH} ${TARGET}/srs-fleet-manager
