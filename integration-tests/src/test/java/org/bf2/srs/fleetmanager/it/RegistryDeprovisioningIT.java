package org.bf2.srs.fleetmanager.it;

import io.apicurio.tenantmanager.api.datamodel.TenantStatusValue;
import io.apicurio.tenantmanager.client.TenantManagerClient;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.infra.DefaultInfraManager;
import org.bf2.srs.fleetmanager.it.util.FleetManagerApi;
import org.bf2.srs.fleetmanager.it.util.Utils;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayNameGeneration(SimpleDisplayName.class)
@ExtendWith(DefaultInfraManager.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistryDeprovisioningIT {

    @Test
    void testDeprovisionRegistryBasic() {

        FleetManagerApi.verifyApiIsSecured();

        var alice = new AccountInfo("testDeprovisionRegistry", "alice", false, 10L);

        var registry1 = new RegistryCreate();
        registry1.setName("registry1");

        var createdRegistry1 = FleetManagerApi.createRegistry(registry1, alice);

        assertNotEquals(RegistryStatusValue.failed, createdRegistry1.getStatus());

        Awaitility.await("registry1 available").atMost(30, SECONDS).pollInterval(5, SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(createdRegistry1.getId(), alice);
                    return reg.getStatus().equals(RegistryStatusValue.ready);
                });

        Registry registry = FleetManagerApi.getRegistry(createdRegistry1.getId(), alice);

        TenantManagerClient tenantManager = Utils.createTenantManagerClient();

        var internalTenant = tenantManager.getTenant(registry.getId());
        assertEquals(TenantStatusValue.READY, internalTenant.getStatus());

        FleetManagerApi.deleteRegistry(createdRegistry1.getId(), alice);

        // We don't have to wait for the status to be RegistryStatusValueRest.deleting, since that happens almost immediately now.
        Utils.simulateRegistryDeletion(tenantManager, createdRegistry1, alice);
    }
}
