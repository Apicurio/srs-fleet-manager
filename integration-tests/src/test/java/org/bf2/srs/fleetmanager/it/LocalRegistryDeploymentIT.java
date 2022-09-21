package org.bf2.srs.fleetmanager.it;

import io.apicurio.tenantmanager.client.TenantManagerClient;
import org.bf2.srs.fleetmanager.it.infra.LocalInfraManager;
import org.bf2.srs.fleetmanager.it.util.FleetManagerApi;
import org.bf2.srs.fleetmanager.it.util.Utils;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.net.HttpURLConnection.HTTP_CONFLICT;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
@DisplayNameGeneration(SimpleDisplayName.class)
@ExtendWith(LocalInfraManager.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocalRegistryDeploymentIT {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void testDeploymentCRUD() {
        var alice = new AccountInfo("alice", "alice", false, 1L);
        // We are using a LocalDeploymentProvider, so the REST API for the deployment configuration is
        // enabled.
        // Verify that the number of instances in 0:
        TenantManagerClient tmc = Utils.createTenantManagerClient();
        Assertions.assertEquals(0, tmc.listTenants(null, 0, 50, null, null).getCount());
        // Verify the local deployment exists
        var deployments = FleetManagerApi.listRegistryDeployments(alice);
        Assertions.assertEquals(1, deployments.size());
        var localDeployment = deployments.get(0);
        Assertions.assertEquals("local", localDeployment.getName());
        // We should be able to delete the deployment, because there are no instances
        FleetManagerApi.deleteRegistryDeployment(alice, localDeployment.getId());
        deployments = FleetManagerApi.listRegistryDeployments(alice);
        Assertions.assertEquals(0, deployments.size());
        // Add the deployment back
        var localDeploymentCreate = new RegistryDeploymentCreateRest();
        localDeploymentCreate.setName(localDeployment.getName());
        localDeploymentCreate.setTenantManagerUrl(localDeployment.getTenantManagerUrl());
        localDeploymentCreate.setRegistryDeploymentUrl(localDeployment.getRegistryDeploymentUrl());
        localDeployment = FleetManagerApi.createRegistryDeployment(alice, localDeploymentCreate);
        // Deployment is re-created
        // Create a new instance
        var registry1 = new RegistryCreate();
        registry1.setName("test-registry-1");
        var aliceRegistry1 = FleetManagerApi.createRegistry(registry1, alice);
        Utils.waitForReady(aliceRegistry1, alice);
        // Try to delete the deployment now and fail
        FleetManagerApi.deleteRegistryDeployment(alice, localDeployment.getId(), HTTP_CONFLICT);
        // Cleanup
        FleetManagerApi.deleteRegistry(aliceRegistry1.getId(), alice);
        Utils.simulateRegistryDeletion(tmc, aliceRegistry1, alice);
    }
}
