package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.model.ResourceType;

public class MockAccountManagementService implements AccountManagementService {

    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType)
            throws TermsRequiredException, ResourceLimitReachedException {
        return "mock-subscription";
    }

    @Override
    public void deleteSubscription(String subscriptionId) {
        //Do nothing, this is just a mock call
    }
}
