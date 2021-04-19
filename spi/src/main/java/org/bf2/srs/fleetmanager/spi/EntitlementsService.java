package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public interface EntitlementsService {

    boolean hasEntitlements(AccountInfo accountInfo, String resourceType, String subscriptionId);
}