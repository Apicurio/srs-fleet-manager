#!/bin/bash

set -eo pipefail

git clone https://github.com/Apicurio/apicurio-registry.git -o upstream apicurio-registry.git
pushd apicurio-registry.git
git checkout master
mvn install -Pprod -Pmultitenancy -pl 'multitenancy/tenant-manager-client' -am
popd

mvn -B install