package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public class MockAccountManagementService implements AccountManagementService {

    //Just return true for the entitlements check call
    @Override
    public boolean hasEntitlements(AccountInfo accountInfo, String resourceType, String clusterId, String productId) {
        return true;
    }
}
