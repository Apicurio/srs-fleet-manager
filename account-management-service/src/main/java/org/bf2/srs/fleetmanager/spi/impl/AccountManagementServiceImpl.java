package org.bf2.srs.fleetmanager.spi.impl;

import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ReservedResource;
import org.bf2.srs.fleetmanager.spi.impl.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ResponseTermsReview;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ClusterAuthorizationResponse;

import java.util.Collections;

/**
 * This service is in charge of check if a given user has the appropriate situation in order to ask for the requested resource
 */
public class AccountManagementServiceImpl implements AccountManagementService {

	// FIXME make these configurable?
    private static final String MAS_SITE_CODE = "ocm";
	private static final String MAS_EVENT_CODE = "onlineService";
	
	private final AccountManagementSystemRestClient restClient;

    public AccountManagementServiceImpl(AccountManagementSystemRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String createResource(AccountInfo accountInfo, String resourceType, String clusterId, String productId) throws TermsRequiredException, ResourceLimitReachedException {

        boolean termsAccepted;
        final TermsReview termsReview = new TermsReview();
        termsReview.setAccountUsername(accountInfo.getAccountUsername());
        termsReview.setEventCode(MAS_EVENT_CODE);
        termsReview.setSiteCode(MAS_SITE_CODE);
        
        final ResponseTermsReview responseTermsReview = restClient.termsReview(termsReview);
        termsAccepted = !responseTermsReview.getTermsRequired();

        if (termsAccepted) {
            final ClusterAuthorization clusterAuthorization = ClusterAuthorization.builder()
                    .accountUsername(accountInfo.getAccountUsername())
                    .productId(productId)
                    .managed(true)
                    .byoc(false)
                    .cloudProviderId("aws")
                    .reserve(true)
                    .availabilityZone("single")
                    .clusterId(clusterId)
                    .resources(Collections.singletonList(ReservedResource.builder().resourceType(resourceType).resourceName(productId).count(1).build()))
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
