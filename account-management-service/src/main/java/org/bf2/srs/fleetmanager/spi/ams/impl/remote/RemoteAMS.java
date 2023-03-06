package org.bf2.srs.fleetmanager.spi.ams.impl.remote;

import io.apicurio.rest.client.VertxHttpClientProvider;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import io.vertx.core.Vertx;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.exception.AccountManagementSystemAuthErrorHandler;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.exception.AccountManagementSystemClientException;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.exception.ExceptionConvert;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.ReservedResource;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.ClusterAuthorizationResponse;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.Organization;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.QuotaCost;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.QuotaCostList;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.RelatedResource;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.ResponseTermsReview;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This service is in charge of check if a given user has the appropriate situation in order to ask for the requested resource
 */
public class RemoteAMS implements AccountManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RemoteAMSProperties amsProperties;
    private final AccountManagementSystemRestClient restClient;

    public RemoteAMS(Vertx vertx, RemoteAMSProperties amsProperties) {
        this.amsProperties = amsProperties;
        this.restClient = createAccountManagementRestClient(vertx);
    }

    private AccountManagementSystemRestClient createAccountManagementRestClient(Vertx vertx) {
        AccountManagementSystemRestClient restClient;
        if (amsProperties.ssoEnabled) {
            ApicurioHttpClient httpClient = new VertxHttpClientProvider(vertx).create(amsProperties.ssoTokenEndpoint, Collections.emptyMap(), null, new AccountManagementSystemAuthErrorHandler());
            final OidcAuth auth = new OidcAuth(httpClient, amsProperties.ssoClientId, amsProperties.ssoClientSecret);
            restClient = new AccountManagementSystemRestClient(vertx, amsProperties.endpoint, Collections.emptyMap(), auth);
        } else {
            restClient = new AccountManagementSystemRestClient(vertx, amsProperties.endpoint, Collections.emptyMap(), null);
        }
        return restClient;
    }

    @Override
    public ResourceType determineAllowedResourceType(AccountInfo accountInfo) throws AccountManagementServiceException {
        try {
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

        } catch (AccountManagementSystemClientException ex) {
            ExceptionConvert.convert(ex);
            return null; // Never returns
        }
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

    @Override
    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException, AccountManagementServiceException {
        try {
            boolean termsAccepted = false;
            String siteCode = amsProperties.termsSiteCode;
            List<String> eventCodes = amsProperties.termsEventCode;

            for (String eventCode : eventCodes) {
                final TermsReview termsReview = new TermsReview();
                termsReview.setAccountUsername(accountInfo.getAccountUsername());
                termsReview.setSiteCode(siteCode);
                termsReview.setEventCode(eventCode);

                // Check if the user has accepted the Terms & Conditions
                final ResponseTermsReview responseTermsReview = restClient.termsReview(termsReview);
                boolean accepted = !responseTermsReview.getTermsRequired();
                // Terms are accepted if *any* of the T&C checks come back as "accepted"
                termsAccepted = termsAccepted || accepted;
            }

            if (!termsAccepted) {
                throw new TermsRequiredException(accountInfo.getAccountUsername());
            }

            // If we're creating an eval instance, don't bother invoking AMS - return a null subscriptionId
            // TODO Workaround: Remove this once we have RHOSRTrial working.
            if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
                log.debug("Creating an eval instance for '{}' in org '{}' without calling AMS.", accountInfo.getAccountUsername(), accountInfo.getOrganizationId());
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
        } catch (AccountManagementSystemClientException ex) {
            ExceptionConvert.convert(ex);
            return null; // Never returns
        }
    }

    @Override
    public void deleteSubscription(String subscriptionId) throws SubscriptionNotFoundServiceException, AccountManagementServiceException {
        try {
            // If the subscriptionId is null, it means we didn't reserve quota in AMS for this
            // instances (likely because it's an Eval instance).
            // TODO Workaround: Remove this once we have RHOSRTrial working.
            if (subscriptionId != null) {
                restClient.deleteSubscription(subscriptionId);
            }
        } catch (AccountManagementSystemClientException ex) {
            ExceptionConvert.convertWithSubscriptionNotFound(ex);
        }
    }

    @Override
    public void close() throws IOException {
        restClient.close();
    }
}
