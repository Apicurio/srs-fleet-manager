package org.bf2.srs.fleetmanager.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.apicurio.rest.client.auth.OidcAuth;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.junit.jupiter.api.Test;
import io.apicurio.multitenant.api.datamodel.ResourceType;
import io.apicurio.multitenant.api.datamodel.TenantResource;
import io.apicurio.multitenant.client.TenantManagerClient;
import io.apicurio.multitenant.client.TenantManagerClientImpl;

public class RegistryProvisioningIT extends SRSFleetManagerBaseIT {

    private FleetManagerApi api = new FleetManagerApi();

    @Test
    void testProvisionRegistry() {

        api.verifyApiIsSecured();

        var alice = new AccountInfo("testProvisionRegistry", "alice", false, 10L);

        //verify static deployments config file feature
        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("test-deployment");
        deployment.setTenantManagerUrl(infra.getTenantManagerUri());
        deployment.setRegistryDeploymentUrl("http://registry-test");
        api.verifyCreateDeploymentNotAllowed(deployment, alice);

        var registry1 = new RegistryCreateRest();
        registry1.setName("test-registry-1");

        var registry1Result = api.createRegistry(registry1, alice);

        assertNotEquals(RegistryStatusValueRest.failed, registry1Result.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
            .until(() -> {
                var reg = api.getRegistry(registry1Result.getId(), alice);
                return reg.getStatus().equals(RegistryStatusValueRest.ready);
            });

        RegistryRest registry = api.getRegistry(registry1Result.getId(), alice);

        String registryUrl = registry.getRegistryUrl();
        assertNotNull(registryUrl);
        var tokens = registryUrl.split("/t/");
        assertTrue(tokens.length == 2);

        String internalTenantId = tokens[1];

        TenantManagerClient tenantManager;
        if (infra.isTenantManagerAuthEnabled()) {
            var tmAuth = infra.getTenantManagerAuthConfig();
            OidcAuth auth = new OidcAuth(tmAuth.tokenEndpoint, tmAuth.clientId, tmAuth.clientSecret);
            tenantManager = new TenantManagerClientImpl(infra.getTenantManagerUri(), Collections.emptyMap(), auth);

            //TODO uncomment
//            {
//                //verify tenant manager auth
//                var invalidTMClient = new TenantManagerClientImpl(infra.getTenantManagerUri(),
//                        new Auth(tmAuth.keycloakUrl, tmAuth.realm, "foo", "baz"));
//                try {
//                    invalidTMClient.deleteTenant("foo");
//                    Assertions.fail("Tenant Manager auth is not working");
//                } catch (TenantManagerClientException e) {
//                    if (!VerificationException.class.isInstance(e)) {
//                        Assertions.fail("Tenant Manager auth did not throw VerificationException");
//                    }
//                }
//            }

        } else {
            tenantManager = new TenantManagerClientImpl(infra.getTenantManagerUri());
        }

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
        api.deleteRegistry(registry1Result.getId(), alice);

    }

    @Test
    public void testPermissions() {

        var alice = new AccountInfo("redhat", "alice", false, 1L);
        var rhadmin = new AccountInfo("redhat", "rhadmin", true, 2L);

        var bob = new AccountInfo("bobsorg", "bob", false, 3L);

        //create registries belonging to alice
        var registry1 = new RegistryCreateRest();
        registry1.setName("test-registry-1");
        var aliceRegistry1 = api.createRegistry(registry1, alice);

        var registry2 = new RegistryCreateRest();
        registry2.setName("test-registry-2");
        var aliceRegistry2 = api.createRegistry(registry2, alice);

        //create one registry belonging to rhadmin
        var registry3 = new RegistryCreateRest();
        registry3.setName("test-registry-3");
        var rhadminRegistry = api.createRegistry(registry3, rhadmin);

        //create one registry belonging to bob, bob is in a different organization
        var registry4 = new RegistryCreateRest();
        registry4.setName("test-registry-4");
        var bobRegistry = api.createRegistry(registry4, bob);

        //################## check read permissions

        //users in same org can see the same registries
        var aliceView = api.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        var rhadminView = api.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertTrue(aliceView.size() == 3);
        assertTrue(rhadminView.size() == 3);

        assertIterableEquals(aliceView, rhadminView);

        //users cannnot see registries of users in other orgs
        var bobView = api.listRegistries(bob);
        assertTrue(bobView.size() == 1);

        api.verifyRegistryNotExists(aliceRegistry1.getId(), bob);

        //################## check delete permissions

        //user cannot delete registry owned by another user in other org
        api.verifyDeleteNotAllowed(bobRegistry.getId(), alice);

        //user cannot delete registry owned by another user in the same org
        api.verifyDeleteNotAllowed(rhadminRegistry.getId(), alice);

        //org admin cannot delete registry owned by another user in other org
        api.verifyDeleteNotAllowed(bobRegistry.getId(), rhadmin);

        //org admin can delete registry owned by another user in the same org
        api.deleteRegistry(aliceRegistry1.getId(), rhadmin);

        aliceView = api.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        rhadminView = api.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertTrue(aliceView.size() == 2);
        assertTrue(rhadminView.size() == 2);

        //user can delete registry owned by himself
        api.deleteRegistry(aliceRegistry2.getId(), alice);

        aliceView = api.listRegistries(alice).stream().map(RegistryRest::getId).collect(Collectors.toList());
        rhadminView = api.listRegistries(rhadmin).stream().map(RegistryRest::getId).collect(Collectors.toList());

        assertTrue(aliceView.size() == 1);
        assertTrue(rhadminView.size() == 1);

    }

}