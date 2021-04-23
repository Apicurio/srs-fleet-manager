package org.bf2.srs.fleetmanager.spi.impl;

import org.b2f.ams.client.AccountManagementSystemRestClient;
import org.b2f.ams.client.exception.TermsRequiredException;
import org.b2f.ams.client.model.request.AccessReview;
import org.b2f.ams.client.model.request.TermsReview;
import org.b2f.ams.client.model.response.ResponseAccessReview;
import org.b2f.ams.client.model.response.ResponseTermsReview;
import org.bf2.srs.fleetmanager.spi.EntitlementsService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

/**
 * This service is in charge of check if a given user has the appropriate situation in order to ask for the requested resource
 */
public class EntitlementsServiceImpl implements EntitlementsService {

    private final AccountManagementSystemRestClient restClient;

    public EntitlementsServiceImpl(AccountManagementSystemRestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * @param accountInfo    the account information for the terms and access requests
     * @param resourceType   the requested resource type
     * @param subscriptionId the subscription id to use in the request
     * @return true if the user can access the requested resource.
     */
    @Override
    public boolean hasEntitlements(AccountInfo accountInfo, String resourceType, String subscriptionId) {

        final TermsReview termsReview = TermsReview.builder()
                .accountUsername(accountInfo.getOrganizationId())
                .build();

        final ResponseTermsReview responseTermsReview = restClient.termsReview(termsReview);

        if (responseTermsReview.getTermsRequired()) {
            throw new TermsRequiredException(responseTermsReview.getAccountId());
        } else {

            final AccessReview accessReview = AccessReview.builder()
                    .accountUsername(accountInfo.getAccountUsername())
                    .organizationId(accountInfo.getOrganizationId())
                    .resourceType(resourceType)
                    .subscriptionId(subscriptionId)
                    .build();

            final ResponseAccessReview responseAccessReview = restClient.accessReview(accessReview);

            return responseAccessReview.getAllowed();
        }
    }
}
