package org.bf2.srs.fleetmanager.it;

import io.apicurio.multitenant.api.datamodel.ResourceType;
import io.apicurio.multitenant.api.datamodel.UpdateRegistryTenantRequest;
import io.apicurio.multitenant.client.TenantManagerClient;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class QuotaPlanIT extends SRSFleetManagerBaseIT {

    @Test
    void testQuotaPlan() throws Exception {

        var alice = new AccountInfo("alice", "alice", false, 1L);

        var registry1 = new RegistryCreate();
        registry1.setName("registry-basic");
        var registry1Result = FleetManagerApi.createRegistry(registry1, alice);

        assertNotEquals(RegistryStatusValue.failed, registry1Result.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(registry1Result.getId(), alice);
                    return reg.getStatus().equals(RegistryStatusValue.ready);
                });

        var bob = new AccountInfo("bob", "bob", false, 2L);

        var registry2 = new RegistryCreate();
        registry2.setName("registry-premium");
        var registry2Result = FleetManagerApi.createRegistry(registry2, bob);

        assertNotEquals(RegistryStatusValue.failed, registry2Result.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(registry2Result.getId(), bob);
                    return reg.getStatus().equals(RegistryStatusValue.ready);
                });

        TenantManagerClient tenantManager = Utils.createTenantManagerClient();

        // basic
        var tenant = tenantManager.getTenant(registry1Result.getId());
        var resources = tenant.getResources();
        Long l = null;
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(10, l);

        // premium
        tenant = tenantManager.getTenant(registry2Result.getId());
        resources = tenant.getResources();
        l = null;
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(100, l);

        // Update the limit value and recheck after forced reconciliation
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                r.setLimit(-1L);
            }
        }
        var ur = new UpdateRegistryTenantRequest();
        ur.setResources(resources);
        tenantManager.updateTenant(registry2Result.getId(), ur);
        // Check updated
        tenant = tenantManager.getTenant(registry2Result.getId());
        resources = tenant.getResources();
        l = null;
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(-1, l);

        // Restart fleet manager(s) so the quota plan is reconciled
        TestInfraManager.getInstance().restartFleetManager();

        tenant = tenantManager.getTenant(registry2Result.getId());
        resources = tenant.getResources();
        l = null;
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(100, l);

        // Delete
        FleetManagerApi.deleteRegistry(registry1Result.getId(), alice);
        FleetManagerApi.deleteRegistry(registry2Result.getId(), bob);
    }
}