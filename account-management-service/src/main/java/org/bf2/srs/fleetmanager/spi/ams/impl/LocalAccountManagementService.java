package org.bf2.srs.fleetmanager.spi.ams.impl;

import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;

import java.util.UUID;

public class LocalAccountManagementService implements AccountManagementService {


    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        //TODO:carnalca limit number of resources created using account information (and injecting storage to the class)
        return UUID.randomUUID().toString();
    }

    public void deleteSubscription(String subscriptionId) throws AccountManagementServiceException, SubscriptionNotFoundServiceException {

    }
}
