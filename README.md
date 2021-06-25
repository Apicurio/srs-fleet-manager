# Service Registry service Fleet Manager

## Quickstart

To build and run the application in the `dev` mode, execute:

```shell script
mvn clean install quarkus:dev -Ddev
```

Notes:

- JDK 11 is required because of the following issue: https://github.com/quarkusio/quarkus/issues/13871 . The problem
  appears when you run the application using an older JDK version that the one used to compile it. Specifically, the
  Docker image uses JDK 11. You can skip the enforcer rule using `-Denforcer.skip`.
- As a result of issues with multi-module projects, you need to use the `install` Maven target when
  running `quarkus:dev`.
- The `-Ddev` property enables `dev` Quarkus profile (even for docker images), and uses
  a standalone database (h2) and other components to avoid dependencies.
- You can add `-Ddebug` property to connect a debugger.

To build a `dev` application image, run:

```shell script
mvn clean install -Ddev -Dquarkus.container-image.build=true
```

which can be executed using:

```shell script
docker run -p 8080:8080 {user}/srs-fleet-manager-core:0.1.0-SNAPSHOT
```

## Building SR-MSA for production use

The application depends on an Apicurio Registry Tenant Manager client.
This dependency is not published at the moment, so you have to build the Tenant Manager
and the dependency as follows:

```shell script
git clone git@github.com:Apicurio/apicurio-registry.git
cd apicurio-registry
make build-tenant-manager
```

Now, you can build the Service API:

```shell script
mvn clean install -Dquarkus.container-image.build=true
```

The production deployment of the application requires:
 - Postgresql database (see `core/src/main/resources/application.properties` for which environment variables to use for configuration)
 - Latest Apicurio Registry deployment running (`2.0.0-SNAPSHOT`)
 - Apicurio Registry Tenant Manager running

See `dist/openshift/README.md` for information about deploying the required components on OpenShift.

## Deploy Postgresql locally

```shell script
docker run -p 5432:5432 -e 'POSTGRES_PASSWORD=postgres' -d postgres
docker run --network host -p 8080:8080 -e 'SERVICE_API_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres' {user}/srs-fleet-manager-core:0.1.0-SNAPSHOT
```

## Configuring auth

In order to configure security you must configure the following values:

|Option|Env. variable|
|---|---|
|Enable authentication |`AUTH_ENABLED`|
|Authentication server url|`KEYCLOAK_URL`|
|Authentication realm|`KEYCLOAK_REALM`|
|Authentication client|`KEYCLOAK_API_CLIENT_ID`|

### Configuring tenant manager security

When security is enabled, the application will also expect the following values to be configured to be able to connect to the tenant-manager in a secure manner:

|Option|Env. variable|
|---|---|
|Tenant manager auth server url |`TENANT_MANAGER_AUTH_SERVER_URL`|
|Tenant manager auth realm|`TENANT_MANAGER_AUTH_SERVER_REALM`|
|Tenant manager auth client|`TENANT_MANAGER_AUTH_CLIENT_ID`|
|Tenant manager auth secret|`TENANT_MANAGER_AUTH_SECRET`|

## Examples

The following are several commands for working with the Service API

* Create a Registry Deployment

```shell script
curl -X POST -H "Content-Type: application/json" \
  -d '{"tenantManagerUrl":"http://tm1.app.example.com"}' \
  http://localhost:8080/api/serviceregistry_mgmt/v1/admin/registryDeployments
```

* List Registry Deployments

```shell script
 curl http://localhost:8080/api/serviceregistry_mgmt/v1/admin/registryDeployments
```

* Create a Registry (Tenant)

```shell script
curl -X POST -H "Content-Type: application/json" \
  -d '{}' \
  http://localhost:8080/api/serviceregistry_mgmt/v1/registries
```

* List Registries (Tenants)

```shell script
curl http://localhost:8080/api/serviceregistry_mgmt/v1/registries
```

* List active Tasks

```shell script
curl http://localhost:8080/api/serviceregistry_mgmt/v1/admin/tasks
```

## Didact demo

Open VSCode on this project, go to `demo/demo.didact.md` file and use ` Ctrl + Shift + V ` to open the Didact view.
You need to be logged to an Openshift 4 cluster to run the demo.