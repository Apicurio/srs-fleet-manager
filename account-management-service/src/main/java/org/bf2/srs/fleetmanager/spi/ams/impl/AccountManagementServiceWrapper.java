package org.bf2.srs.fleetmanager.spi.ams.impl;

import io.micrometer.core.annotation.Timed;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.common.metrics.Constants;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.FaultToleranceConstants;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryUnwrap;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrap;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrapperException;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_AMS_SUBSCRIPTION_ID;

@ApplicationScoped
@UnlessBuildProfile("test")
public class AccountManagementServiceWrapper implements AccountManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    AccountManagementServiceProducer producer;

    private AccountManagementService delegate;

    @PostConstruct
    void init() {
        this.delegate = producer.produces();
        requireNonNull(this.delegate);
    }

    @PreDestroy
    void destroy() {
        try {
            this.close();
        } catch (IOException e) {
            log.warn("Error found closing the Account Management Service:", e);
        }
    }

    @Timed(value = Constants.AMS_DETERMINE_ALLOWED_INSTANCE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited
    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        return delegate.determineAllowedResourceType(accountInfo);
    }

    @Timed(value = Constants.AMS_CREATE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractResult = KEY_AMS_SUBSCRIPTION_ID)
    // Do not use fault tolerance annotations here.
    // They may cause orphan subscriptions being created in cases when the AMS REST call times out in the client,
    // but AMS still performs the reservation.
    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        return delegate.createResource(accountInfo, resourceType);
    }

    @Timed(value = Constants.AMS_DELETE_TIMER, description = Constants.AMS_TIMER_DESCRIPTION)
    @Audited(extractParameters = {"0", KEY_AMS_SUBSCRIPTION_ID})
    @Timeout(FaultToleranceConstants.TIMEOUT_MS)
    @RetryUnwrap
    @Retry(retryOn = {RetryWrapperException.class}) // 3 retries, 200ms jitter
    @RetryWrap
    @Override
    public void deleteSubscription(String subscriptionId) throws SubscriptionNotFoundServiceException, AccountManagementServiceException {
        delegate.deleteSubscription(subscriptionId);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
