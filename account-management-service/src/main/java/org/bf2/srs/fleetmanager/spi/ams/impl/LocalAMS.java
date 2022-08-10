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

public class LocalAMS implements AccountManagementService {

    private final LocalAMSProperties properties;
    private final ResourceStorage storage;

    public LocalAMS(LocalAMSProperties properties, ResourceStorage storage) {
        this.properties = properties;
        this.storage = storage;
    }

    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        if (properties.maxInstancesPerOrgId != null && properties.maxInstancesPerOrgId > 0) {
            var count = storage.getRegistryCountPerOrgId(accountInfo.getOrganizationId());
            if (count == properties.maxInstancesPerOrgId)
                throw new ResourceLimitReachedException();
        }
        return UUID.randomUUID().toString();
    }

    public void deleteSubscription(String subscriptionId) throws AccountManagementServiceException, SubscriptionNotFoundServiceException {
        // NOOP
    }
}
