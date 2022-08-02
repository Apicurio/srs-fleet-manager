### Docker-compose and Quarkus based installation

#### Overview

This setup contains a fully configured Apicurio Registry multitenant deployment already integrated with Keycloak. It contains a shell script which will configure the environment. Currently, every application is routed to the host network without SSL support. This is a development version, do not use it in a production environment!

Here is the port mapping:
- 8080 for the Registry
- 8081 for Fleet Manager
- 8585 for Tenant Manager

If you want to clean your env (e.g. the database info etc), you have to remove the already existing docker volumes. The easiest way is to stop your running compose stack, and prune your volumes:

```
docker system prune --volumes
```

#### Starting the environment

You can start the whole stack with these commands:

In order to be able to start the deployment, you need to set the environment variable: TENANT_MANAGER_CLIENT_SECRET

```
docker-compose -f docker-compose-registry-full.yml up
```