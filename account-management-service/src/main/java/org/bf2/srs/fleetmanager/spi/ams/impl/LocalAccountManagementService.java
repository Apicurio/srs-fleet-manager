package org.bf2.srs.fleetmanager.spi.ams.impl;

import io.micrometer.core.annotation.Timed;
import org.bf2.srs.fleetmanager.common.metrics.Constants;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;

import java.util.UUID;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_AMS_SUBSCRIPTION_ID;

public class LocalAccountManagementService implements AccountManagementService {

    @Timed(value = Constants.AMS_DETERMINE_ALLOWED_INSTANCE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited
    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    @Timed(value = Constants.AMS_CREATE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractResult = KEY_AMS_SUBSCRIPTION_ID)
    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        //TODO:carnalca limit number of resources created using account information (and injecting storage to the class)
        return UUID.randomUUID().toString();
    }

    @Timed(value = Constants.AMS_DELETE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractParameters = {"0", KEY_AMS_SUBSCRIPTION_ID})
    @Override
    public void deleteSubscription(String subscriptionId) throws AccountManagementServiceException, SubscriptionNotFoundServiceException {

    }
}
