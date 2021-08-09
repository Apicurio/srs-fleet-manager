package org.bf2.srs.fleetmanager.it;

import io.apicurio.multitenant.api.datamodel.ResourceType;
import io.apicurio.multitenant.api.datamodel.TenantResource;
import io.apicurio.multitenant.client.TenantManagerClient;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.Utils.TenantInfo;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RegistryProvisioningIT extends SRSFleetManagerBaseIT {

    @Test
    void testProvisionRegistry() {

        FleetManagerApi.verifyApiIsSecured();

        var alice = new AccountInfo("testProvisionRegistry", "alice", false, 10L);

        //verify static deployments config file feature
        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("test-deployment");
        deployment.setTenantManagerUrl(infra.getTenantManagerUri());
        deployment.setRegistryDeploymentUrl("http://registry-test");
        FleetManagerApi.verifyCreateDeploymentNotAllowed(deployment, alice);

        var registry1 = new RegistryCreateRest();
        registry1.setName("test-registry-1");

        var registry1Result = FleetManagerApi.createRegistry(registry1, alice);

        assertNotEquals(RegistryStatusValueRest.failed, registry1Result.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(registry1Result.getId(), alice);
                    return reg.getStatus().equals(RegistryStatusValueRest.ready);
                });

        RegistryRest registry = FleetManagerApi.getRegistry(registry1Result.getId(), alice);

        String internalTenantId = Utils.getTenantIdFromUrl(registry.getRegistryUrl());

        TenantManagerClient tenantManager = Utils.createTenantManagerClient();

        var internalTenant = tenantManager.getTenant(internalTenantId);

        var resources = internalTenant.getResources();

        TenantResource maxTotalSchemas = null;
        for (var r : resources) {
            if (r.getType() == ResourceType.MAX_TOTAL_SCHEMAS_COUNT) {
                maxTotalSchemas = r;
            }
        }
        assertNotNull(maxTotalSchemas);
        assertEquals(10, maxTotalSchemas.getLimit());

        //TODO e2e test check limits are applied

        // Delete
        FleetManagerApi.deleteRegistry(registry1Result.getId(), alice);
    }

    @Test
    void testPermissions() {

        var alice = new AccountInfo("redhat", "alice", false, 1L);
        var rhadmin = new AccountInfo("redhat", "rhadmin", true, 2L);

        var bob = new AccountInfo("bobsorg", "bob", false, 3L);

        //create registries belonging to alice
        var registry1 = new RegistryCreateRest();
        registry1.setName("test-registry-1");
        var aliceRegistry1 = FleetManagerApi.createRegistry(registry1, alice);

        var registry2 = new RegistryCreateRest();
        registry2.setName("test-registry-2");
        var aliceRegistry2 = FleetManagerApi.createRegistry(registry2, alice);

        //create one registry belonging to rhadmin
        var registry3 = new RegistryCreateRest();
        registry3.setName("test-registry-3");
        var rhadminRegistry = FleetManagerApi.createRegistry(registry3, rhadmin);

        //create one registry belonging to bob, bob is in a different organization
        var registry4 = new RegistryCreateRest();
        registry4.setName("test-registry-4");
        var bobRegistry = FleetManagerApi.createRegistry(registry4, bob);

        //################## check read permissions

        //users in same org can see the same registries
        var aliceView = FleetManagerApi.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        var rhadminView = FleetManagerApi.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertEquals(3, aliceView.size());
        assertEquals(3, rhadminView.size());

        assertIterableEquals(aliceView, rhadminView);

        //users cannnot see registries of users in other orgs
        var bobView = FleetManagerApi.listRegistries(bob);
        assertEquals(1, bobView.size());

        FleetManagerApi.verifyRegistryNotExists(aliceRegistry1.getId(), bob);

        //################## check delete permissions

        //user cannot delete registry owned by another user in other org
        FleetManagerApi.verifyDeleteNotAllowed(bobRegistry.getId(), alice);

        //user cannot delete registry owned by another user in the same org
        FleetManagerApi.verifyDeleteNotAllowed(rhadminRegistry.getId(), alice);

        //org admin cannot delete registry owned by another user in other org
        FleetManagerApi.verifyDeleteNotAllowed(bobRegistry.getId(), rhadmin);

        // Wait until we have the Registry URL
        aliceRegistry1 = Utils.waitForReady(aliceRegistry1, rhadmin);
        var aliceRegistry1Tid = Utils.getTenantIdFromUrl(aliceRegistry1.getRegistryUrl());

        //org admin can delete registry owned by another user in the same org
        FleetManagerApi.deleteRegistry(aliceRegistry1.getId(), rhadmin);

        var tenantManager = Utils.createTenantManagerClient();
        Utils.waitForDeletion(tenantManager, List.of(
                TenantInfo.builder().registryId(aliceRegistry1.getId()).tenantId(aliceRegistry1Tid).accountInfo(rhadmin).build()
        ));

        aliceView = FleetManagerApi.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        rhadminView = FleetManagerApi.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertEquals(2, aliceView.size());
        assertEquals(2, rhadminView.size());

        // Wait until we have the Registry URL
        aliceRegistry2 = Utils.waitForReady(aliceRegistry2, alice);
        var aliceRegistry2Tid = Utils.getTenantIdFromUrl(aliceRegistry2.getRegistryUrl());

        //user can delete registry owned by himself
        FleetManagerApi.deleteRegistry(aliceRegistry2.getId(), alice);

        Utils.waitForDeletion(tenantManager, List.of(
                TenantInfo.builder().registryId(aliceRegistry2.getId()).tenantId(aliceRegistry2Tid).accountInfo(alice).build()
        ));

        aliceView = FleetManagerApi.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        rhadminView = FleetManagerApi.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertEquals(1, aliceView.size());
        assertEquals(1, rhadminView.size());
    }
}