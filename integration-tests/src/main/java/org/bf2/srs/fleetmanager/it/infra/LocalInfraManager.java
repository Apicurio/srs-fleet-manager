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
import org.bf2.srs.fleetmanager.it.component.CompoundComponent;
import org.bf2.srs.fleetmanager.it.component.DummyRegistryComponent;
import org.bf2.srs.fleetmanager.it.component.Environments;
import org.bf2.srs.fleetmanager.it.component.FleetManagerComponent;
import org.bf2.srs.fleetmanager.it.component.KeycloakMockComponent;
import org.bf2.srs.fleetmanager.it.component.PostgresqlComponent;
import org.bf2.srs.fleetmanager.it.component.TenantManagerComponent;

import static org.bf2.srs.fleetmanager.it.component.CompoundComponent.*;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class LocalInfraManager extends AbstractInfraManager {

    @Override
    protected CompoundComponent buildInfra() throws Exception {
        var c = new CompoundComponent();

        var keycloak = new KeycloakMockComponent(Environments.empty());
        c.addAndStart(C_KEYCLOAK, keycloak);

        var registry = new DummyRegistryComponent(Environments.empty());
        c.addAndStart(C_REGISTRY, registry);

        var psqlcTM = new PostgresqlComponent(Environments.empty(), "tenant-manager");
        c.addAndStart(C_POSTGRESQL_TM, psqlcTM);

        var tenantManagerEnv = Environments.forTenantManager(psqlcTM, registry);
        var tm = new TenantManagerComponent(tenantManagerEnv, null);
        c.addAndStart(C_TM, tm);

        var psqlcFM = new PostgresqlComponent(Environments.empty(), "fleet-manager");
        c.addAndStart(C_POSTGRESQL_FM, psqlcFM);

        var fleetManagerPort = 8080;
        var fmEnv = Environments.forFleetManagerLocal(psqlcFM, keycloak.getAuthConfig());
        var fm1 = new FleetManagerComponent(fmEnv, fleetManagerPort, "node1");
        c.addAndStart(C_FM1, fm1);

        RestAssured.baseURI = fm1.getFleetManagerUri(); // TODO Use both nodes?

        return c;
    }
}
