package org.b2f.ams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apicurio.rest.client.JdkHttpClient;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.request.Request;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import org.b2f.ams.client.exception.AccountManagementErrorHandler;
import org.b2f.ams.client.exception.AccountManagementSystemClientException;
import org.b2f.ams.client.model.request.ClusterAuthorization;
import org.b2f.ams.client.model.request.SelfTermsReview;
import org.b2f.ams.client.model.request.TermsReview;
import org.b2f.ams.client.model.response.ClusterAuthorizationResponse;
import org.b2f.ams.client.model.response.Error;
import org.b2f.ams.client.model.response.ResponseTermsReview;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AccountManagementSystemRestClient {

    private final ApicurioHttpClient client;
    private final ObjectMapper mapper;
    private final OidcAuth auth;

    public AccountManagementSystemRestClient(String endpoint, Map<String, Object> configs, OidcAuth auth) {
        this.client = new JdkHttpClient(endpoint, configs, auth, new AccountManagementErrorHandler());
        this.mapper = new ObjectMapper();
        this.auth = auth;
    }

    public ResponseTermsReview termsReview(TermsReview termsReview) {
        try {
            return this.client.sendRequest(new Request.RequestBuilder<ResponseTermsReview>()
                    .path(Paths.TERMS_REVIEW_PATH)
                    .data(mapper.writeValueAsString(termsReview))
                    .responseType(new TypeReference<ResponseTermsReview>() {
                    })
                    .build());
        } catch (JsonProcessingException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public ResponseTermsReview selfTermsReview(SelfTermsReview selfTermsReview) {
        try {
            return this.client.sendRequest(new Request.RequestBuilder<ResponseTermsReview>()
                    .path(Paths.SELF_TERMS_REVIEW)
                    .data(mapper.writeValueAsString(selfTermsReview))
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
                    .path(Paths.CLUSTER_AUTHORIZATION)
                    .data(mapper.writeValueAsString(clusterAuthorization))
                    .responseType(new TypeReference<ClusterAuthorizationResponse>() {
                    })
                    .build());
        } catch (JsonProcessingException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }
}
