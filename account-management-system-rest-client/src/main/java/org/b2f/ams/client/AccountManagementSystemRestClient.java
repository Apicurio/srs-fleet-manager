package org.b2f.ams.client;

import org.b2f.ams.model.request.AccessReview;
import org.b2f.ams.model.request.TermsReview;

import java.net.http.HttpClient;
import java.util.Map;

public class AccountManagementSystemRestClient {

    HttpClient httpClient;

    public AccountManagementSystemRestClient(Map<String, String> configs) {
        final HttpClient.Builder httpClientBuilder = handleConfiguration(configs);

        httpClient = httpClientBuilder.build();
    }

    private HttpClient.Builder handleConfiguration(Map<String, String> configs) {
        return null;
    }

    public AccessReview accessReview() {

        return null;
    }

    public TermsReview termsReview() {
        return null;
    }
}
