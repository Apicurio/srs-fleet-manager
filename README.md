# Service Registry - Managed Service API

## Running in dev mode

```shell script
./mvnw compile quarkus:dev
```

```shell script
./mvnw compile quarkus:dev -Ddebug
```

## Building SR-MSA

The application can be packaged using:

```shell script
./mvnw package
```

```shell script
./mvnw install
```

## Examples

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
