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
                .set("REGISTRY_ROUTE_URL", registry.getBaseUrl());
    }

    public static Environment forTenantManagerAuth(PostgresqlComponent psqlc, DummyRegistryComponent registry, AuthConfig tmAuthConfig) {

        return Environment.create()
                .inherit(forTenantManager(psqlc, registry))
                .set("AUTH_ENABLED", "true")
                .set("KEYCLOAK_URL", tmAuthConfig.getKeycloakUrl())
                .set("KEYCLOAK_REALM", tmAuthConfig.getRealm())
                .set("KEYCLOAK_API_CLIENT_ID", tmAuthConfig.getClientId());
    }

    public static Environment forFleetManagerDB(PostgresqlComponent psqlc) {

        return Environment.create()
                .set("SERVICE_API_DATASOURCE_URL", psqlc.getDatasourceUrl())
                .set("SERVICE_API_DATASOURCE_USERNAME", psqlc.getUsername())
                .set("SERVICE_API_DATASOURCE_PASSWORD", psqlc.getPassword());
    }

    public static Environment forFleetManagerAuth(AuthConfig fmAuthConfig) {
        return Environment.create()
                .set("AUTH_ENABLED", "true")
                .set("KEYCLOAK_URL", fmAuthConfig.getKeycloakUrl())
                .set("KEYCLOAK_REALM", fmAuthConfig.getRealm())
                .set("KEYCLOAK_API_CLIENT_ID", fmAuthConfig.getClientId())
                .set("TENANT_MANAGER_AUTH_ENABLED", "false");
    }

    public static Environment forFleetManagerAMSMock(AMSMockComponent ams) {
        return Environment.create()
                .set("FM_AMS_TYPE", "REMOTE")
                .set("AMS_SSO_ENABLED", "false")
                .set("AMS_URL", ams.getBaseUrl());
    }

    public static Environment forFleetManagerTMAuth(AuthConfig tmAuthConfig) {

        return Environment.create()
                .set("TENANT_MANAGER_AUTH_ENABLED", "true")
                .set("TENANT_MANAGER_AUTH_SERVER_URL", tmAuthConfig.getKeycloakUrl())
                .set("TENANT_MANAGER_AUTH_SERVER_REALM", tmAuthConfig.getRealm())
                .set("TENANT_MANAGER_AUTH_CLIENT_ID", tmAuthConfig.getClientId())
                .set("TENANT_MANAGER_AUTH_SECRET", tmAuthConfig.getClientSecret());
    }

    public static Environment forFleetManagerDefault(PostgresqlComponent psqlc, AMSMockComponent ams, AuthConfig fmAuthConfig) {

        return Environment.create()
                .inherit(base())
                .inherit(forFleetManagerDB(psqlc))
                .inherit(forFleetManagerAuth(fmAuthConfig))
                .inherit(forFleetManagerAMSMock(ams))
                .set("REGISTRY_DEPLOYMENTS_CONFIG_FILE", DEPLOYMENTS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_CONFIG_FILE", PLANS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_DEFAULT", "basic");
    }

    public static Environment forFleetManagerDefaultTMAuth(PostgresqlComponent psqlc, AMSMockComponent ams, AuthConfig tmAuthConfig, AuthConfig fmAuthConfig) {

        return Environment.create()
                .inherit(base())
                .inherit(forFleetManagerDB(psqlc))
                .inherit(forFleetManagerAuth(fmAuthConfig))
                .inherit(forFleetManagerTMAuth(tmAuthConfig))
                .inherit(forFleetManagerAMSMock(ams))
                .set("REGISTRY_DEPLOYMENTS_CONFIG_FILE", DEPLOYMENTS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_CONFIG_FILE", PLANS_CONFIG_FILE)
                .set("REGISTRY_QUOTA_PLANS_DEFAULT", "basic");
    }

    public static Environment forFleetManagerLocal(PostgresqlComponent psqlc, AuthConfig fmAuthConfig) {

        return Environment.create()
                .inherit(base())
                .inherit(forFleetManagerDB(psqlc))
                .inherit(forFleetManagerAuth(fmAuthConfig))
                .set("FM_AMS_LOCAL_MAX_INSTANCES_PER_ORG_ID", "2");
    }

    private Environments() {
    }
}
