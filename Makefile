APICURIO_TENANT_REPO?=https://github.com/Apicurio/apicurio-tenant-manager.git
APICURIO_TENANT_BRANCH?=main

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


build-tenant-manager-deps: pull-apicurio-deps
	cd multitenancy; mvn install -Pprod -DskipTests --no-transfer-progress
.PHONY: build-tenant-manager-deps

update-tenant-manager-dep-version: pull-apicurio-deps
	@echo "Updating apicurio deps to version "$(shell (cd multitenancy && mvn help:evaluate -Dexpression=project.version -q -DforceStdout))
	mvn versions:set-property -Dproperty=apicurio-tenant-manager-client.version -DgenerateBackupPoms=false -DnewVersion=$(shell (cd multitenancy && mvn help:evaluate -Dexpression=project.version -q -DforceStdout))
.PHONY: update-tenant-manager-dep-version

pull-apicurio-deps:
	rm -rf multitenancy
	git clone --depth 1 -b $(APICURIO_TENANT_BRANCH) $(APICURIO_TENANT_REPO) multitenancy
.PHONY: pull-apicurio-deps
