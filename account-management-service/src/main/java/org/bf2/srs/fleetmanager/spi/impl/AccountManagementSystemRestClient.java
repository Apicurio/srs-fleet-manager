package org.bf2.srs.fleetmanager.spi.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementErrorHandler;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemClientException;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.impl.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ClusterAuthorizationResponse;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Organization;
import org.bf2.srs.fleetmanager.spi.impl.model.response.OrganizationList;
import org.bf2.srs.fleetmanager.spi.impl.model.response.QuotaCostList;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ResponseTermsReview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.apicurio.rest.client.JdkHttpClient;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.request.Operation;
import io.apicurio.rest.client.request.Request;
import io.apicurio.rest.client.spi.ApicurioHttpClient;

public class AccountManagementSystemRestClient {

    private final ApicurioHttpClient client;
    private final ObjectMapper mapper;

    public AccountManagementSystemRestClient(String endpoint, Map<String, Object> configs, OidcAuth auth) {
        this.client = new JdkHttpClient(endpoint, configs, auth, new AccountManagementErrorHandler());
        this.mapper = new ObjectMapper();
    }

    public ResponseTermsReview termsReview(TermsReview termsReview) {
        try {
            return this.client.sendRequest(new Request.RequestBuilder<ResponseTermsReview>()
                    .operation(Operation.POST)
                    .path(Paths.TERMS_REVIEW_PATH)
                    .data(mapper.writeValueAsString(termsReview))
                    .responseType(new TypeReference<ResponseTermsReview>() {
                    })
                    .build());
        } catch (JsonProcessingException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public ClusterAuthorizationResponse clusterAuthorization(ClusterAuthorization clusterAuthorization) {
        try {
            return this.client.sendRequest(new Request.RequestBuilder<ClusterAuthorizationResponse>()
                    .operation(Operation.POST)
                    .path(Paths.CLUSTER_AUTHORIZATION)
                    .data(mapper.writeValueAsString(clusterAuthorization))
                    .responseType(new TypeReference<ClusterAuthorizationResponse>() {
                    })
                    .build());
        } catch (JsonProcessingException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public void deleteSubscription(String subscriptionId) {
        this.client.sendRequest(new Request.RequestBuilder<Void>()
                .operation(Operation.DELETE)
                .path(Paths.SUBSCRIPTIONS)
                .pathParams(Collections.singletonList(subscriptionId))
                .responseType(new TypeReference<Void>() {})
                .build());
    }

    public Organization getOrganizationByExternalId(String externalOrgId) {
        String search = "external_id='ORG_ID'".replace("ORG_ID", externalOrgId);
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("search", Collections.singletonList(search));
        OrganizationList rval = this.client.sendRequest(new Request.RequestBuilder<OrganizationList>()
                .operation(Operation.GET)
                .path(Paths.ORGANIZATIONS_PATH)
                .queryParams(queryParams)
                .responseType(new TypeReference<OrganizationList>() {})
                .build());

        if (rval.getTotal() < 1) {
            throw new AccountManagementSystemClientException("Organization not found with external id: " + externalOrgId);
        }

        return rval.getItems().get(0);
    }

    public QuotaCostList getQuotaCostList(String orgId, boolean fetchRelatedResources) {
        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("fetchRelatedResources", Collections.singletonList(String.valueOf(fetchRelatedResources)));
        QuotaCostList rval = this.client.sendRequest(new Request.RequestBuilder<QuotaCostList>()
                .operation(Operation.GET)
                .path(Paths.QUOTA_COST_PATH)
                .pathParams(Collections.singletonList(orgId))
                .queryParams(queryParams)
                .responseType(new TypeReference<QuotaCostList>() {})
                .build());
        return rval;
    }
}
