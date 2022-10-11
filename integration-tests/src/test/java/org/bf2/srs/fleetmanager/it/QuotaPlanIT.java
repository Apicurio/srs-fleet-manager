package org.bf2.srs.fleetmanager.it;

import io.apicurio.tenantmanager.api.datamodel.ResourceType;
import io.apicurio.tenantmanager.api.datamodel.UpdateApicurioTenantRequest;
import io.apicurio.tenantmanager.client.TenantManagerClient;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.component.FleetManagerComponent;
import org.bf2.srs.fleetmanager.it.infra.DefaultInfraManager;
import org.bf2.srs.fleetmanager.it.infra.InfraHolder;
import org.bf2.srs.fleetmanager.it.util.FleetManagerApi;
import org.bf2.srs.fleetmanager.it.util.Utils;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.bf2.srs.fleetmanager.it.component.CompoundComponent.C_FM1;
import static org.bf2.srs.fleetmanager.it.component.CompoundComponent.C_FM2;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(SimpleDisplayName.class)
@ExtendWith(DefaultInfraManager.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuotaPlanIT {

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
            if (r.getType().equals(ResourceType.MAX_TOTAL_SCHEMAS_COUNT)) {
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
            if (r.getType().equals(ResourceType.MAX_TOTAL_SCHEMAS_COUNT)) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(100, l);

        // Update the limit value and recheck after forced reconciliation
        for (var r : resources) {
            if (r.getType().equals(ResourceType.MAX_TOTAL_SCHEMAS_COUNT)) {
                r.setLimit(-1L);
            }
        }
        var ur = new UpdateApicurioTenantRequest();
        ur.setResources(resources);
        tenantManager.updateTenant(registry2Result.getId(), ur);
        // Check updated
        tenant = tenantManager.getTenant(registry2Result.getId());
        resources = tenant.getResources();
        l = null;
        for (var r : resources) {
            if (r.getType().equals(ResourceType.MAX_TOTAL_SCHEMAS_COUNT)) {
                l = r.getLimit();
            }
        }
        assertNotNull(l);
        assertEquals(-1, l);

        // Restart fleet manager(s) so the quota plan is reconciled
        InfraHolder.getInstance().getComponent().get(C_FM1, FleetManagerComponent.class).get().restart();
        InfraHolder.getInstance().getComponent().get(C_FM2, FleetManagerComponent.class).get().restart();

        tenant = tenantManager.getTenant(registry2Result.getId());
        resources = tenant.getResources();
        l = null;
        for (var r : resources) {
            if (r.getType().equals(ResourceType.MAX_TOTAL_SCHEMAS_COUNT)) {
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