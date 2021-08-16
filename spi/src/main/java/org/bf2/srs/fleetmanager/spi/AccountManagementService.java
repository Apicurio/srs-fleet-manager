package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public interface AccountManagementService {

    /**
     * Creates a resource for the given user
     *
     * @param accountInfo  the account information for requests
     * @param resourceType the requested resource type
     * @param clusterId    the clusterId to be used in the request
     * @param productId    the service registry product id
     * @return the id of the subscription.
     */
    String createResource(AccountInfo accountInfo, String resourceType, String clusterId, String productId) throws TermsRequiredException, ResourceLimitReachedException;

    /**
     * Delete a subscription by id
     *
     * @param subscriptionId the identifier of the subscription to be deleted
     */
    void deleteSubscription(String subscriptionId);
}