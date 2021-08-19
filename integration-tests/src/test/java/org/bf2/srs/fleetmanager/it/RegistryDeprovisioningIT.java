package org.bf2.srs.fleetmanager.it;

import io.apicurio.multitenant.api.datamodel.TenantStatusValue;
import io.apicurio.multitenant.api.datamodel.UpdateRegistryTenantRequest;
import io.apicurio.multitenant.client.TenantManagerClient;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RegistryDeprovisioningIT extends SRSFleetManagerBaseIT {

    @Test
    void testDeprovisionRegistryBasic() {

        FleetManagerApi.verifyApiIsSecured();

        var alice = new AccountInfo("testDeprovisionRegistry", "alice", false, 10L);

        var registry1 = new RegistryCreateRest();
        registry1.setName("registry1");

        var createdRegistry1 = FleetManagerApi.createRegistry(registry1, alice);

        assertNotEquals(RegistryStatusValueRest.failed, createdRegistry1.getStatus());

        Awaitility.await("registry1 available").atMost(30, SECONDS).pollInterval(5, SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(createdRegistry1.getId(), alice);
                    return reg.getStatus().equals(RegistryStatusValueRest.ready);
                });

        RegistryRest registry = FleetManagerApi.getRegistry(createdRegistry1.getId(), alice);

        String registry1TenantId = Utils.getTenantIdFromUrl(registry.getRegistryUrl());

        TenantManagerClient tenantManager = Utils.createTenantManagerClient();

        var internalTenant = tenantManager.getTenant(registry1TenantId);
        assertEquals(TenantStatusValue.READY, internalTenant.getStatus());

        FleetManagerApi.deleteRegistry(createdRegistry1.getId(), alice);

        Awaitility.await("registry1 deprovisioning stated").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> {
                    // This may be too slow to catch RegistryStatusValueRest.deprovision status,
                    // so we'll wait for RegistryStatusValueRest.deleting.
                    var reg = FleetManagerApi.getRegistry(createdRegistry1.getId(), alice);
                    return RegistryStatusValueRest.deleting.equals(reg.getStatus());
                });

        Awaitility.await("registry1 deleting initiated").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> {
                    var tenant1 = tenantManager.getTenant(registry1TenantId);
                    return TenantStatusValue.TO_BE_DELETED.equals(tenant1.getStatus());
                });

        var req = new UpdateRegistryTenantRequest();
        req.setStatus(TenantStatusValue.DELETED);
        tenantManager.updateTenant(registry1TenantId, req);

        Awaitility.await("registry1 deleted").atMost(CheckRegistryDeletedTask.builder().build().getSchedule().getInterval().getSeconds() * 2, SECONDS).pollInterval(5, SECONDS)
                .until(() -> {
                    try {
                        FleetManagerApi.verifyRegistryNotExists(createdRegistry1.getId(), alice);
                        return true;
                    } catch (AssertionError ex) {
                        return false;
                    }
                });
    }
}