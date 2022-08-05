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

package org.bf2.srs.fleetmanager.it;

import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.infra.LocalAMSInfraManager;
import org.bf2.srs.fleetmanager.it.util.FleetManagerApi;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@DisplayNameGeneration(SimpleDisplayName.class)
@ExtendWith(LocalAMSInfraManager.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocalAMSIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAMSIT.class);

    @Test
    void testLimits() {
        // First organization
        var alice = new AccountInfo("alice", "alice", false, 1L);
        var alice1 = createRegistryOk("alice1", alice);
        createRegistryOk("alice2", alice);
        // Max 2 instances
        var error = createRegistryError("alice3", alice, HTTP_CONFLICT, org.bf2.srs.fleetmanager.rest.publicapi.beans.Error.class);
        Assertions.assertEquals("SRS-MGMT-7", error.getCode());
        // First organization
        var bob = new AccountInfo("bob", "bob", false, 2L);
        createRegistryOk("bob1", bob);
        createRegistryOk("bob2", bob);
        // Max 2 instances
        error = createRegistryError("bob3", bob, HTTP_CONFLICT, org.bf2.srs.fleetmanager.rest.publicapi.beans.Error.class);
        Assertions.assertEquals("SRS-MGMT-7", error.getCode());
        // Delete Alice's Registry to make space
        deleteRegistryOk(alice1.getId(), alice);
        createRegistryOk("alice4", alice);
        error = createRegistryError("alice4", alice, HTTP_CONFLICT, org.bf2.srs.fleetmanager.rest.publicapi.beans.Error.class);
        Assertions.assertEquals("SRS-MGMT-7", error.getCode());
        // Bob still does not have quota
        error = createRegistryError("bob3", bob, HTTP_CONFLICT, org.bf2.srs.fleetmanager.rest.publicapi.beans.Error.class);
        Assertions.assertEquals("SRS-MGMT-7", error.getCode());
    }

    private Registry createRegistryOk(String name, AccountInfo accountInfo) {
        var registry = new RegistryCreate();
        registry.setName(name);
        var res = FleetManagerApi.createRegistry(registry, accountInfo);
        assertNotEquals(RegistryStatusValue.failed, res.getStatus());
        Awaitility.await("registry is ready")
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(res.getId(), accountInfo);
                    return RegistryStatusValue.ready.equals(reg.getStatus());
                });
        return res;
    }

    private <T> T createRegistryError(String name, AccountInfo accountInfo, int expectedStatusCode, Class<T> resultType) {
        var registry = new RegistryCreate();
        registry.setName(name);
        return FleetManagerApi.createRegistry(registry, accountInfo, expectedStatusCode, resultType);
    }

    private void deleteRegistryOk(String registryId, AccountInfo accountInfo) {
        FleetManagerApi.deleteRegistry(registryId, accountInfo);
        Awaitility.await("registry is deleted")
                .pollInterval(5, TimeUnit.SECONDS)
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        FleetManagerApi.verifyRegistryNotExists(registryId, accountInfo);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }
}
