### Docker-compose and Quarkus based installation

#### Overview

This setup contains a fully configured Apicurio Registry multitenant deployment already integrated with Keycloak. It contains a shell script which will configure the environment. Currently, every application is routed to the host network without SSL support. This is a development version, do not use it in a production environment!

Here is the port mapping:
- 8090 for Keycloak
- 8080 for the Registry
- 8082 for Fleet Manager
- 8081 for Tenant Manager

#### Setup

The folder contains a bash script to make the initialization. The script will create the configuration files based on your IP address.
The scripts will create 1 file:
- .env
- 
The easiest way is to run the script. At the end of the run, it will print the admin password for Keycloak, and the URLs for the services. Like this:

```

Registry API URL: 192.168.1.231:8080
Fleet Manager URL: 192.168.1.231:8081
Tenant Manager URL: 192.168.1.231:8082

```

Please copy these values somewhere where you can find them easily!

#### Script based setup

```
./setup.sh {IP_OF_YOUR_HOST}
```

Note: make sure you use the external IP address of your host here.  `localhost` and `127.0.0.1` will not work.

#### Environment customisation

After the successfull run of the setup script, a file called `.env` will appear. This file contains the customisable properties of the environment. Every property is already filled in, so this is only for customization. You can set your passwords, URL's, and the versions of the components. The default version is the `latest`.

If you want to change these settings after you already started the stack, you have to remove the already existing docker volumes. The easiest way is to stop your running compose stack, and prune your volumes:

```
docker system prune --volumes
```

A simple "reset" script is also included, it will remove the generated config files for you.

```
./reset_env.sh
```

#### Starting the environment

Once your configs are generated, you can start the whole stack with these commands:

```
docker-compose -f docker-compose-registry-full.yml up

```

To clear the environment, please run these commands:

```
docker system prune --volumes
./reset_env.sh
```