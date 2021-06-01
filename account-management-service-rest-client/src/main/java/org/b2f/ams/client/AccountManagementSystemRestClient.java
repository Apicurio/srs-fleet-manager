package org.b2f.ams.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.b2f.ams.client.auth.Auth;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AccountManagementSystemRestClient {

    private final HttpClient client;
    private final String endpoint;
    private final ObjectMapper mapper;
    private final Auth auth;

    public AccountManagementSystemRestClient(String endpoint, Map<String, String> configs, Auth auth) {
        final HttpClient.Builder ClientBuilder = handleConfiguration(configs);

        ClientBuilder.version(HttpClient.Version.HTTP_1_1);

        this.endpoint = endpoint;
        this.client = ClientBuilder.build();
        this.mapper = new ObjectMapper();
        this.auth = auth;
    }

    private HttpClient.Builder handleConfiguration(Map<String, String> configs) {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        return clientBuilder;
    }

    public ResponseTermsReview termsReview(TermsReview termsReview) {
        try {
            return sendRequest(URI.create(endpoint + Paths.TERMS_REVIEW_PATH), new TypeReference<ResponseTermsReview>() {
            }, this.mapper.writeValueAsBytes(termsReview));
        } catch (IOException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public ResponseTermsReview selfTermsReview(SelfTermsReview selfTermsReview) {
        try {
            return sendRequest(URI.create(endpoint + Paths.SELF_TERMS_REVIEW), new TypeReference<ResponseTermsReview>() {
            }, this.mapper.writeValueAsBytes(selfTermsReview));
        } catch (IOException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public ClusterAuthorizationResponse clusterAuthorization(ClusterAuthorization clusterAuthorization) {
        try {
            return sendRequest(URI.create(endpoint + Paths.CLUSTER_AUTHORIZATION), new TypeReference<ClusterAuthorizationResponse>() {
            }, this.mapper.writeValueAsBytes(clusterAuthorization));
        } catch (IOException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }

    public <T> T sendRequest(URI requestPath, TypeReference<T> typeReference, byte[] bodyData) {
        try {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            if (auth != null) {
                auth.apply(headers);
            }

            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(requestPath)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyData));

            headers.forEach(reqBuilder::header);

            HttpResponse<InputStream> res = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
            if (res.statusCode() == 200) {
                return this.mapper.readValue(res.body(), typeReference);
            }
            throw new AccountManagementSystemClientException(this.mapper.readValue(res.body(), Error.class));
        } catch (IOException | InterruptedException e) {
            throw new AccountManagementSystemClientException(e);
        }
    }
}
