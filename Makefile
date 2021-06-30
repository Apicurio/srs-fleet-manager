APICURIO_REGISTRY_REPO?=https://github.com/Apicurio/apicurio-registry.git
APICURIO_REGISTRY_BRANCH?=master

# builds and runs unit tests for srs-fleet-manager app
build:
	mvn install $(EXTRA_ARGS)
.PHONY: build

# builds tenant-manager required dependencies and builds and runs integration tests for srs-fleet-manager app
pr-check: build-tenant-manager-deps build
	mvn verify -Pit -pl integration-tests
.PHONY: pr-check

build-deploy: pr-check
.PHONY: build-deploy

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