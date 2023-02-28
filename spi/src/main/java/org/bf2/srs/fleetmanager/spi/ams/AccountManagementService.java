package org.bf2.srs.fleetmanager.spi.ams;

import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;

public interface AccountManagementService {

    /**
     * Figure out which type of resource is allowed for the given account info.
     */
    ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException;

    /**
     * Creates a resource for the given user and return a subscriptionId that can later
     * be used to delete the resource.
     *
     * @param accountInfo  the account information for requests
     * @param resourceType the requested resource type
     * @return the id of the subscription
     */
    String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException;

    /**
     * Delete a subscription by id
     *
     * @param subscriptionId the identifier of the subscription to be deleted
     */
    void deleteSubscription(String subscriptionId) throws AccountManagementServiceException, SubscriptionNotFoundServiceException;
}