# Guidelines for Contributing to this Project

## Code Generation

Code in the following packages (except `.impl`) is generated from an OpenAPI schema:

- `core/src/main/resources/srs-fleet-manager.json` -> `org.bf2.srs.fleetmanager.rest.publicapi`
- `core/src/main/resources/srs-fleet-manager-private.json` -> `org.bf2.srs.fleetmanager.rest.privateapi`

Do not edit it directly. We do not have an automated process set up to generate the code yet, so it requires manual intervention.
Use the following procedure to re-generate the code if the corresponding OpenAPI schema has changed:

1. Go to https://studio.apicur.io/ and log in.
1. Click `Import API` to import one of the OpenAPI schemas.
1. Optionally, make some changes and save (do not forget to update the schema in this repo afterwards).
1. Go to the main page for the schema (where the blue `Edit API` button is).
1. Click the button with vertical three dots and click `Generate Project`.
1. Choose to generate bare code (without the entire Maven project),
   and enter the give package name.
   
