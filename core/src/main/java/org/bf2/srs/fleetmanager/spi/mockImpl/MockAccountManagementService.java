package org.bf2.srs.fleetmanager.spi.mockImpl;

import io.micrometer.core.annotation.Timed;
import io.quarkus.arc.DefaultBean;

import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.common.metrics.Constants;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_AMS_SUBSCRIPTION_ID;


@DefaultBean
@ApplicationScoped
public class MockAccountManagementService implements AccountManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    void init() {
        log.info("Using Mock Account Management Service.");
    }

    @Timed(value = Constants.AMS_DETERMINE_ALLOWED_INSTANCE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited
    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    @Timed(value = Constants.AMS_CREATE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractResult = KEY_AMS_SUBSCRIPTION_ID)
    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType)
            throws TermsRequiredException, ResourceLimitReachedException {
        return "mock-subscription";
    }

    @Timed(value = Constants.AMS_DELETE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractParameters = {"0", KEY_AMS_SUBSCRIPTION_ID})
    @Override
    public void deleteSubscription(String subscriptionId) {
        //Do nothing, this is just a mock call
    }

    @Override
    public void close() {

    }
}
