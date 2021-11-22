APICURIO_REGISTRY_REPO?=https://github.com/Apicurio/apicurio-registry.git
APICURIO_REGISTRY_BRANCH?=mas-sr

COMMON_ARGS=-Dmaven.javadoc.skip=true --no-transfer-progress -DtrimStackTrace=false


help:
	@echo ""
	@echo "Please use \`make <target>' where <target> is one of:-"
	@grep -E '^\.PHONY: [a-zA-Z_-]+ .*?## .*$$' $(MAKEFILE_LIST)  | awk 'BEGIN {FS = "(: |##)"}; {printf "\033[36m%-42s\033[0m %s\n", $$2, $$3}'
	@echo ""
	@echo "=> EXTRA_ARGS: You can pass additional build args by overriding the value of this variable. By Default, it doesn't pass any additional flags."
	@echo ""
.PHONY: help


dev-build:
	mvn install -Ddev $(COMMON_ARGS) $(EXTRA_ARGS)
.PHONY: dev-build ## Builds the simplified development version (using mocks instead of external dependencies)


dev-run:
	mvn install quarkus:dev -Ddev $(COMMON_ARGS) $(EXTRA_ARGS)
.PHONY: dev-run ## Builds the simplified development version, and runs it using Quarkus dev mode


build:
	mvn install $(COMMON_ARGS) $(EXTRA_ARGS)
.PHONY: build ## Builds and runs unit tests


integration-tests:
	mvn verify -Pit -pl integration-tests $(COMMON_ARGS) $(EXTRA_ARGS)
.PHONY: integration-tests  ## Builds and runs integration tests


build-project: build-tenant-manager-deps build
.PHONY: build-project  ## Builds the required dependencies (Tenant Manager) and then builds SRS Fleet Manager


pr-check: build-project integration-tests
.PHONY: pr-check ## Builds SRS Fleet Manager with the required dependencies, and executes integration tests


build-tenant-manager-deps: pull-apicurio-registry
	cd apicurio-registry; mvn install -Pprod -Pmultitenancy -pl 'multitenancy/tenant-manager-client,multitenancy/tenant-manager-api' -am -DskipTests
.PHONY: build-tenant-manager-deps

update-tenant-manager-dep-version: pull-apicurio-registry
	mvn versions:set-property -Dproperty=apicurio-registry-tenant-manager-client.version -DgenerateBackupPoms=false -DnewVersion=$(shell xq .project.version apicurio-registry/pom.xml -r)
.PHONY: update-tenant-manager-dep-version

pull-apicurio-registry:
ifeq (,$(wildcard ./apicurio-registry))
	git clone -b $(APICURIO_REGISTRY_BRANCH) $(APICURIO_REGISTRY_REPO) apicurio-registry
else
	cd apicurio-registry; git pull
endif
.PHONY: pull-apicurio-registry
