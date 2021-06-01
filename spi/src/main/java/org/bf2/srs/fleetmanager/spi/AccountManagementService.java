package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public interface AccountManagementService {

    /**
     * Checks if a given user has a valid entitlement
     *
     * @param accountInfo    the account information for the terms and access requests
     * @param resourceType   the requested resource type
     * @param clusterId      the cluster where we want to create the resource
     * @param productId      the product that we want to create
     * @return the recently create subscription id.
     */
    String createResource(AccountInfo accountInfo, String resourceType, String clusterId, String productId);

    /**
     * Checks if a given user has a valid entitlement
     *
     * @param accountInfo    the account information for the terms and access requests
     * @param resourceType   the requested resource type
     * @param clusterId the clusterId to be used in the request
     * @return true if the user has a valid entitlement.
     */
    boolean hasEntitlements(AccountInfo accountInfo, String resourceType, String clusterId);
}