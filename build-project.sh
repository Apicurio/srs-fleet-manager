#!/bin/bash

set -eo pipefail

git clone https://gitlab.cee.redhat.com/service-registry/srs-service-registry.git -o upstream srs-service-registry.git
pushd srs-service-registry.git
git checkout master
mvn install -Pprod -Pmultitenancy -pl 'multitenancy/tenant-manager-client' -am
popd

mvn -B install