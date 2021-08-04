package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public class MockAccountManagementService implements AccountManagementService {

    //Just return true for the entitlements check call
    @Override
    public String createResource(AccountInfo accountInfo, String resourceType, String clusterId, String productId) {
        return "mock-subscription";
    }

    @Override
    public void deleteSubscription(String subscriptionId) {
        //Do nothing, this is just a mock call
    }
}
