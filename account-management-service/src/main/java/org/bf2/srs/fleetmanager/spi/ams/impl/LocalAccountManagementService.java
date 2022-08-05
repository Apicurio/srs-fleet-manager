package org.bf2.srs.fleetmanager.spi.ams.impl;

import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;

import java.util.UUID;

public class LocalAccountManagementService implements AccountManagementService {


    private final ResourceStorage storage;
    private final Integer maxInstancesPerOrgId;

    /**
     * @param maxInstancesPerOrgId Limit the number of instances per each org.
     *                             If null or not positive, the limit is disabled.
     */
    public LocalAccountManagementService(ResourceStorage storage, Integer maxInstancesPerOrgId) {
        this.storage = storage;
        this.maxInstancesPerOrgId = maxInstancesPerOrgId;
    }

    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        if (maxInstancesPerOrgId != null && maxInstancesPerOrgId > 0) {
            var count = storage.getRegistryCountPerOrgId(accountInfo.getAccountUsername());
            if (count == maxInstancesPerOrgId)
                throw new ResourceLimitReachedException();
        }
        return UUID.randomUUID().toString();
    }

    public void deleteSubscription(String subscriptionId) throws AccountManagementServiceException, SubscriptionNotFoundServiceException {
        // NOOP
    }
}
