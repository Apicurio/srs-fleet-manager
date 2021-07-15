APICURIO_REGISTRY_REPO?=https://github.com/Apicurio/apicurio-registry.git
APICURIO_REGISTRY_BRANCH?=mas-sr

# builds and runs unit tests for srs-fleet-manager app
build:
	mvn install -Dmaven.javadoc.skip=true --no-transfer-progress -DtrimStackTrace=false $(EXTRA_ARGS)
.PHONY: build

integration-tests:
	mvn verify -Pit -pl integration-tests -Dmaven.javadoc.skip=true --no-transfer-progress -DtrimStackTrace=false
.PHONY: integration-tests

# builds tenant-manager required dependencies and builds srs-fleet-manager app
build-project: build-tenant-manager-deps build
.PHONY: build-project

# builds srs-fleet-manager app and it's dependencies and integration tests
pr-check: build-project integration-tests
.PHONY: pr-check

build-tenant-manager-deps: pull-apicurio-registry
	cd apicurio-registry; mvn install -Pprod -Pmultitenancy -pl 'multitenancy/tenant-manager-client,multitenancy/tenant-manager-api' -am -DskipTests
.PHONY: build-tenant-manager-deps

pull-apicurio-registry:
ifeq (,$(wildcard ./apicurio-registry))
	git clone -b $(APICURIO_REGISTRY_BRANCH) $(APICURIO_REGISTRY_REPO) apicurio-registry
else
	cd apicurio-registry; git pull
endif
.PHONY: pull-apicurio-registry