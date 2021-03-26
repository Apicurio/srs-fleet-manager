package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.EntitlementsService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public class MockEntitlementsService implements EntitlementsService {

    //Just return true for the entitlements check call
    @Override
    public boolean hasEntitlements(AccountInfo accountInfo) {
        return true;
    }
}
