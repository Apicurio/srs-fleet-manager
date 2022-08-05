package org.bf2.srs.fleetmanager.it.component;

import org.bf2.srs.fleetmanager.it.AuthConfig;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class Environments {

    private static final String DEPLOYMENTS_CONFIG_FILE = "./src/test/resources/deployments.yaml";
    private static final String PLANS_CONFIG_FILE = "./src/test/resources/plans.yaml";

    public static Environment empty() {
        return Environment.create();
    }

    public static Environment base() {

        return Environment.create()
                .set("LOG_LEVEL", "DEBUG")
                .set("SRS_LOG_LEVEL", "DEBUG");
    }

    public static Environment forTenantManager(PostgresqlComponent psqlc, DummyRegistryComponent registry) {

        return Environment.create()
                .inherit(base())
                .set("DATASOURCE_URL", psqlc.getDatasourceUrl())
                .set("DATASOURCE_USERNAME", "postgres")
                .set("DATASOURCE_PASSWORD", "postgres")
                .set("REGISTRY_ROUTE_URL", registry.getBaseUrl())
                .set("AUTH_ENABLED", "false");
    }

    public static Environment forTenantManagerAuth(PostgresqlComponent psqlc, DummyRegistryComponent registry, AuthConfig tmAuthConfig) {

        return Environment.create()
                .inherit(forTenantManager(psqlc, registry))
                .set("AUTH_ENABLED", "true")
                .set("KEYCLOAK_URL", tmAuthConfig.getKeycloakUrl())
                .set("KEYCLOAK_REALM", tmAuthConfig.getRealm())
                .set("KEYCLOAK_API_CLIENT_ID", tmAuthConfig.getClientId());
    }

    public static Environment forFleetManager(PostgresqlComponent psqlc) {

        return Environment.create()
                .inherit(base())
                .set("SERVICE_API_DATASOURCE_URL", psqlc.getDatasourceUrl())
                .set("SERVICE_API_DATASOURCE_USERNAME", psqlc.getUsername())
                .set("SERVICE_API_DATASOURCE_PASSWORD", psqlc.getPassword())
                .set("REGISTRY_DEPLOYMENTS_CONFIG_FILE", DEPLOYMENTS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_CONFIG_FILE", PLANS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_DEFAULT", "basic");
    }

    public static Environment forFleetManagerAuth(PostgresqlComponent psqlc, AuthConfig fmAuthConfig) {
        return Environment.create()
                .inherit(forFleetManager(psqlc))
                .set("AUTH_ENABLED", "true")
                .set("KEYCLOAK_URL", fmAuthConfig.getKeycloakUrl())
                .set("KEYCLOAK_REALM", fmAuthConfig.getRealm())
                .set("KEYCLOAK_API_CLIENT_ID", fmAuthConfig.getClientId())
                .set("TENANT_MANAGER_AUTH_ENABLED", "false");
    }

    public static Environment forFleetManagerAuthAMSMock(PostgresqlComponent psqlc, AMSMockComponent ams, AuthConfig fmAuthConfig) {
        return Environment.create()
                .inherit(forFleetManagerAuth(psqlc, fmAuthConfig))
                .set("AMS_SSO_ENABLED", "false")
                .set("AMS_URL", ams.getBaseUrl());
    }

    public static Environment forFleetManagerAuthLocalAMS(PostgresqlComponent psqlc, AuthConfig fmAuthConfig) {
        return Environment.create()
                .inherit(forFleetManagerAuth(psqlc, fmAuthConfig))
                .set("USE_LOCAL_AMS", "true")
                .set("AMS_LOCAL_MAX_INSTANCES_PER_ORG_ID", "2");
    }

    public static Environment forFleetManagerAuthTMAuth(PostgresqlComponent psqlc, AMSMockComponent ams, AuthConfig tmAuthConfig, AuthConfig fmAuthConfig) {

        return Environment.create()
                .inherit(forFleetManagerAuthAMSMock(psqlc, ams, fmAuthConfig))
                .set("TENANT_MANAGER_AUTH_ENABLED", "true")
                .set("TENANT_MANAGER_AUTH_SERVER_URL", tmAuthConfig.getKeycloakUrl())
                .set("TENANT_MANAGER_AUTH_SERVER_REALM", tmAuthConfig.getRealm())
                .set("TENANT_MANAGER_AUTH_CLIENT_ID", tmAuthConfig.getClientId())
                .set("TENANT_MANAGER_AUTH_SECRET", tmAuthConfig.getClientSecret());
    }

    private Environments() {
    }
}
