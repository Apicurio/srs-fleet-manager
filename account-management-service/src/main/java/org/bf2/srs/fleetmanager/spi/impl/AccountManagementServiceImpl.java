package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.rest.client.auth.OidcAuth;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemAuthErrorHandler;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ReservedResource;
import org.bf2.srs.fleetmanager.spi.impl.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ClusterAuthorizationResponse;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Organization;
import org.bf2.srs.fleetmanager.spi.impl.model.response.QuotaCost;
import org.bf2.srs.fleetmanager.spi.impl.model.response.QuotaCostList;
import org.bf2.srs.fleetmanager.spi.impl.model.response.RelatedResource;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ResponseTermsReview;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.model.ResourceType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_AMS_SUBSCRIPTION_ID;

/**
 * This service is in charge of check if a given user has the appropriate situation in order to ask for the requested resource
 */
@UnlessBuildProfile("test")
@ApplicationScoped
public class AccountManagementServiceImpl implements AccountManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "account-management-system.url")
    String endpoint;

    @ConfigProperty(name = "sso.token.endpoint")
    String ssoTokenEndpoint;

    @ConfigProperty(name = "sso.client-id")
    String ssoClientId;

    @ConfigProperty(name = "sso.client-secret")
    String ssoClientSecret;

    @ConfigProperty(name = "sso.enabled")
    boolean ssoEnabled;

    @Inject
    AccountManagementServiceProperties amsProperties;

    private AccountManagementSystemRestClient restClient;

    @PostConstruct
    void init() {
        log.info("Using Account Management Service with Account Management URL: {}", endpoint);
        if (ssoEnabled) {
            final OidcAuth auth = new OidcAuth(ssoTokenEndpoint, ssoClientId, ssoClientSecret, Optional.of(new AccountManagementSystemAuthErrorHandler()));
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
        } else {
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), null);
        }
    }

    @Audited
    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) {
        Organization organization = restClient.getOrganizationByExternalId(accountInfo.getOrganizationId());
        String orgId = organization.getId();

        // Check QuotaCostList for a RHOSR entry with "allowed" quota > 0.  If found, then
        // return "Standard" as the resource type to create.
        QuotaCostList quotaCostList = restClient.getQuotaCostList(orgId, true);
        if (quotaCostList.getSize() > 0) {
            for (QuotaCost quotaCost : quotaCostList.getItems()) {
                // We only care about QuotaCost with "allowed" > 0 and with at least one related resource.
                if (quotaCost.getAllowed() != null && quotaCost.getAllowed() > 0 &&
                        quotaCost.getRelated_resources() != null && !quotaCost.getRelated_resources().isEmpty() &&
                        isRhosrStandardQuota(quotaCost)) {
                    return ResourceType.REGISTRY_INSTANCE_STANDARD;
                }
            }
        }

        // Default to only allow eval.
        return ResourceType.REGISTRY_INSTANCE_EVAL;
    }

    /**
     * Returns true if the given QuotaCost object represents standard RHOSR quota.
     *
     * @param quotaCost
     */
    private boolean isRhosrStandardQuota(QuotaCost quotaCost) {
        for (RelatedResource relatedResource : quotaCost.getRelated_resources()) {
            if (amsProperties.standardProductId.equals(relatedResource.getProduct()) && amsProperties.standardResourceName.equals(relatedResource.getResource_name())) {
                return true;
            }
        }
        return false;
    }

    @Audited(extractResult = KEY_AMS_SUBSCRIPTION_ID)
    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException {

        boolean termsAccepted;
        final TermsReview termsReview = new TermsReview();
        termsReview.setAccountUsername(accountInfo.getAccountUsername());
        termsReview.setEventCode(amsProperties.termsEventCode);
        termsReview.setSiteCode(amsProperties.termsSiteCode);

        // Check if the user has accepted the Terms & Conditions
        final ResponseTermsReview responseTermsReview = restClient.termsReview(termsReview);
        termsAccepted = !responseTermsReview.getTermsRequired();
        if (!termsAccepted) {
            throw new TermsRequiredException(accountInfo.getAccountUsername());
        }

        // If we're creating an eval instance, don't bother invoking AMS - return a null subscriptionId
        // TODO Workaround: Remove this once we have RHOSRTrial working.
        if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
            log.warn("Creating an eval instance for '{}' in org '{}' without calling AMS. " +
                    "This is a temporary workaround.", accountInfo.getAccountUsername(), accountInfo.getOrganizationId());
            return null;
        }

        // Set the productId and resourceName based on if it's an Eval or Standard instance
        String productId = amsProperties.standardProductId;
        String resourceName = amsProperties.standardResourceName;
        if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
            productId = amsProperties.evalProductId;
            resourceName = amsProperties.evalResourceName;
        }

        // Build a quota resource ID to pass to AMS
        final var quotaResource = ReservedResource.builder()
                .resourceType(amsProperties.resourceType)
                .byoc(false)
                .resourceName(resourceName)
                .billingModel("marketplace")
                .availabilityZone("single")
                .count(1)
                .build();

        // Create the cluster authorization REST operation input
        final ClusterAuthorization clusterAuthorization = ClusterAuthorization.builder()
                .accountUsername(accountInfo.getAccountUsername())
                .productId(productId)
                .managed(true)
                .byoc(false)
                .cloudProviderId("aws")
                .reserve(true)
                .availabilityZone("single")
                .clusterId(UUID.randomUUID().toString())
                .resources(Collections.singletonList(quotaResource))
                .build();

        // Consume quota from AMS via the AMS REST API
        final ClusterAuthorizationResponse clusterAuthorizationResponse = restClient.clusterAuthorization(clusterAuthorization);

        if (clusterAuthorizationResponse.getAllowed()) {
            return clusterAuthorizationResponse.getSubscription().getId();
        } else {
            // User not allowed to create resource
            throw new ResourceLimitReachedException();
        }
    }

    @Audited(extractParameters = {"0", KEY_AMS_SUBSCRIPTION_ID})
    @Override
    public void deleteSubscription(String subscriptionId) {
        // If the subscriptionId is null, it means we didn't reserve quota in AMS for this
        // instances (likely because it's an Eval instance).
        // TODO Workaround: Remove this once we have RHOSRTrial working.
        if (subscriptionId != null) {
            restClient.deleteSubscription(subscriptionId);
        }
    }
}
