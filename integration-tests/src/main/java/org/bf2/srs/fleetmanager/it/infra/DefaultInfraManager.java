/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.it.infra;

import io.restassured.RestAssured;
import org.bf2.srs.fleetmanager.it.AuthConfig;
import org.bf2.srs.fleetmanager.it.component.AMSMockComponent;
import org.bf2.srs.fleetmanager.it.component.CompoundComponent;
import org.bf2.srs.fleetmanager.it.component.DummyRegistryComponent;
import org.bf2.srs.fleetmanager.it.component.Environment;
import org.bf2.srs.fleetmanager.it.component.Environments;
import org.bf2.srs.fleetmanager.it.component.FleetManagerComponent;
import org.bf2.srs.fleetmanager.it.component.KeycloakMockComponent;
import org.bf2.srs.fleetmanager.it.component.PostgresqlComponent;
import org.bf2.srs.fleetmanager.it.component.TenantManagerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bf2.srs.fleetmanager.it.component.CompoundComponent.*;
import static org.bf2.srs.fleetmanager.it.component.Util.getMandatoryEnvVar;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class DefaultInfraManager extends AbstractInfraManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInfraManager.class);

    private static final String TENANT_MANAGER_AUTH_ENABLED = "TENANT_MANAGER_AUTH_ENABLED";
    private static final String MAS_SSO_URL = "MAS_SSO_URL";
    private static final String MAS_SSO_REALM = "MAS_SSO_REALM";
    private static final String MAS_SSO_CLIENT_ID = "MAS_SSO_CLIENT_ID";
    private static final String MAS_SSO_CLIENT_SECRET = "MAS_SSO_CLIENT_SECRET";

    private final int fleetManagerPort = 8080;

    @Override
    protected CompoundComponent buildInfra() throws Exception {
        var c = new CompoundComponent();

        var tmAuthEnabled = enableAuth();

        var keycloak = new KeycloakMockComponent(Environments.empty());
        c.addAndStart(C_KEYCLOAK, keycloak);

        var registry = new DummyRegistryComponent(Environments.empty());
        c.addAndStart(C_REGISTRY, registry);

        var ams = new AMSMockComponent(Environments.empty());
        c.addAndStart(C_AMS, ams);

        var psqlcTM = new PostgresqlComponent(Environments.empty(), "tenant-manager");
        c.addAndStart(C_POSTGRESQL_TM, psqlcTM);

        TenantManagerComponent tm = null;
        //TODO adapt tests to work with and without authentication
        if (tmAuthEnabled) {
            LOGGER.info("Tenant Manager authentication is enabled");

            var tmAuthConfig = AuthConfig.builder()
                    .keycloakUrl(getMandatoryEnvVar(MAS_SSO_URL))
                    .realm(getMandatoryEnvVar(MAS_SSO_REALM))
                    .clientId(getMandatoryEnvVar(MAS_SSO_CLIENT_ID))
                    .clientSecret(getMandatoryEnvVar(MAS_SSO_CLIENT_SECRET))
                    .build();

            var tenantManagerEnv = Environments.forTenantManagerAuth(psqlcTM, registry, tmAuthConfig);
            tm = new TenantManagerComponent(tenantManagerEnv, tmAuthConfig);
        } else {
            var tenantManagerEnv = Environments.forTenantManager(psqlcTM, registry);
            tm = new TenantManagerComponent(tenantManagerEnv, null);
        }

        c.addAndStart(C_TM, tm);

        var psqlcFM = new PostgresqlComponent(Environments.empty(), "fleet-manager");
        c.addAndStart(C_POSTGRESQL_FM, psqlcFM);

        Environment fmEnv = null;
        if (tmAuthEnabled) {
            fmEnv = Environments.forFleetManagerDefaultTMAuth(psqlcFM, ams, tm.getAuthConfig(), keycloak.getAuthConfig());
        } else {
            fmEnv = Environments.forFleetManagerDefault(psqlcFM, ams, keycloak.getAuthConfig());
        }

        var fm1 = new FleetManagerComponent(fmEnv, fleetManagerPort, "node1");
        c.addAndStart(C_FM1, fm1);

        var fm2 = new FleetManagerComponent(fmEnv, fleetManagerPort + 1, "node2");
        c.addAndStart(C_FM2, fm2);

        RestAssured.baseURI = fm1.getFleetManagerUri(); // TODO Use both nodes?

        return c;
    }

    private boolean enableAuth() {
        String authEnabledVar = System.getenv(TENANT_MANAGER_AUTH_ENABLED);
        return "true".equals(authEnabledVar);
    }
}
