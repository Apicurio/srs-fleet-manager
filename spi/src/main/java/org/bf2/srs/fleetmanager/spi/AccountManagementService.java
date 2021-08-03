package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public interface AccountManagementService {

    /**
     * Checks if a given user has a valid entitlement
     *
     * @param accountInfo    the account information for the terms and access requests
     * @param resourceType   the requested resource type
     * @param clusterId      the clusterId to be used in the request
     * @param productId      the service registry product id
     * @return true if the user has a valid entitlement.
     */
    boolean hasEntitlements(AccountInfo accountInfo, String resourceType, String clusterId, String productId);
}