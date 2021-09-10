package org.bf2.srs.fleetmanager.spi.impl;

import java.util.Collections;
import java.util.UUID;

import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
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

/**
 * This service is in charge of check if a given user has the appropriate situation in order to ask for the requested resource
 */
public class AccountManagementServiceImpl implements AccountManagementService {

	private final AccountManagementSystemRestClient restClient;

	private final AccountManagementServiceProperties amsProperties;

    /**
     * Constructor.
     * @param restClient
     * @param amsProperties
     */
    public AccountManagementServiceImpl(AccountManagementSystemRestClient restClient, AccountManagementServiceProperties amsProperties) {
        this.restClient = restClient;
        this.amsProperties = amsProperties;
    }

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
                        isRhosrStandardQuota(quotaCost))
                {
                    return ResourceType.REGISTRY_INSTANCE_STANDARD;
                }
            }
        }

        // Default to only allow eval.
        return ResourceType.REGISTRY_INSTANCE_EVAL;
    }

    /**
     * Returns true if the given QuotaCost object represents standard RHOSR quota.
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
    public String createResource(AccountInfo accountInfo, ResourceType resourceType) throws TermsRequiredException, ResourceLimitReachedException {

        boolean termsAccepted;
        final TermsReview termsReview = new TermsReview();
        termsReview.setAccountUsername(accountInfo.getAccountUsername());
        termsReview.setEventCode(amsProperties.termsEventCode);
        termsReview.setSiteCode(amsProperties.termsSiteCode);

        final ResponseTermsReview responseTermsReview = restClient.termsReview(termsReview);
        termsAccepted = !responseTermsReview.getTermsRequired();

        if (termsAccepted) {

            String productId = amsProperties.standardProductId;
            String resourceName = amsProperties.standardResourceName;
            if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
                productId = amsProperties.evalProductId;
                resourceName = amsProperties.evalResourceName;
            }

            final var quotaResource = ReservedResource.builder()
                    .resourceType(amsProperties.resourceType)
                    .byoc(false)
                    .resourceName(resourceName)
                    .billingModel("marketplace")
                    .availabilityZone("single")
                    .count(1)
                    .build();

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

            final ClusterAuthorizationResponse clusterAuthorizationResponse = restClient.clusterAuthorization(clusterAuthorization);

            if (clusterAuthorizationResponse.getAllowed()) {
                return clusterAuthorizationResponse.getSubscription().getId();
            } else {
                //User not allowed to create resource
                throw new ResourceLimitReachedException();
            }
        } else {
            throw new TermsRequiredException(accountInfo.getAccountUsername());
        }
    }

    @Override
    public void deleteSubscription(String subscriptionId) {
        restClient.deleteSubscription(subscriptionId);
    }
}
