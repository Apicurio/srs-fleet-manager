package org.bf2.srs.fleetmanager.spi.mockImpl;

import io.quarkus.arc.DefaultBean;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.model.ResourceType;
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

    @Audited
    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) {
        return ResourceType.REGISTRY_INSTANCE_STANDARD;
    }

    @Audited(extractResult = KEY_AMS_SUBSCRIPTION_ID)
    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType)
            throws TermsRequiredException, ResourceLimitReachedException {
        return "mock-subscription";
    }

    @Audited(extractParameters = {"0", KEY_AMS_SUBSCRIPTION_ID})
    @Override
    public void deleteSubscription(String subscriptionId) {
        //Do nothing, this is just a mock call
    }
}
