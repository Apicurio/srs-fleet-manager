# Service Registry - Managed Service API

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
docker run -p 8080:8080 {user}/service-registry-managed-service-api-core:0.1.0-SNAPSHOT
```

## Building SR-MSA for production use

The application depends on an Apicurio Registry Tenant Manager client.
This dependency is not published at the moment, so you have to build the Tenant Manager
and the dependency as follows:

```shell script
git clone git@github.com:Apicurio/apicurio-registry.git
cd apicurio-registry
git checkout multitenant-sr-demo
make
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
docker run --network host -p 8080:8080 -e 'SERVICE_API_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres' {user}/service-registry-managed-service-api-core:0.1.0-SNAPSHOT
```

## Configuring auth

When a new Registry deployment is provisioned, the application will expect the following values to be also provisioned and valid:

|Option|Env. variable|
|---|---|
|Authentication server url |`AUTH_ADMIN_URL`|
|Authentication admin realm|`AUTH_ADMIN_REALM`|
|Authentication admin client|`AUTH_ADMIN_CLIENT_ID`|
|Authentication admin username|`AUTH_ADMIN_USERNAME`|
|Authentication admin password|`AUTH_ADMIN_PASSWORD`|
|Authentication admin grant type|`AUTH_ADMIN_GRANT_TYPE`|

The service api will use those values in order to connect to the authentication server to create the resources that the Registry deployment will need.

## Examples

The following are several commands for working with the Service API

* Create a Registry Deployment

```shell script
curl -X POST -H "Content-Type: application/json" \
  -d '{"tenantManagerUrl":"http://tm1.app.example.com"}' \
  http://localhost:8080/api/v1/admin/registry-deployments
```

* List Registry Deployments

```shell script
 curl http://localhost:8080/api/v1/admin/registry-deployments
```

* Create a Registry (Tenant)

```shell script
curl -X POST -H "Content-Type: application/json" \
  -d '{}' \
  http://localhost:8080/api/v1/registries
```

* List Registries (Tenants)

```shell script
curl http://localhost:8080/api/v1/registries
```

* List active Tasks

```shell script
curl http://localhost:8080/api/v1/admin/tasks
```
