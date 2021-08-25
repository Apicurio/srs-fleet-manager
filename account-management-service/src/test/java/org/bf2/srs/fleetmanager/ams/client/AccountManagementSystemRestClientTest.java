package org.bf2.srs.fleetmanager.ams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bf2.srs.fleetmanager.spi.impl.AccountManagementSystemRestClient;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.impl.model.request.ReservedResource;
import org.bf2.srs.fleetmanager.spi.impl.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ResponseTermsReview;
import org.bf2.srs.fleetmanager.spi.impl.model.response.ClusterAuthorizationResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class AccountManagementSystemRestClientTest {

    private static final AmsWireMockServer amsWireMockServer = new AmsWireMockServer();
    private static AccountManagementSystemRestClient accountManagementSystemRestClient;

    @BeforeAll
    public static void startServer() throws JsonProcessingException {
        final String mockServerUrl = amsWireMockServer.start();
        accountManagementSystemRestClient = new AccountManagementSystemRestClient(mockServerUrl, Collections.emptyMap(), null);
    }

    @Test
    public void termsAcceptedTest() {
        final TermsReview termsReview = TermsReview.builder()
                .accountUsername("test.account.username")
                .build();

        final ResponseTermsReview responseTermsReview = accountManagementSystemRestClient.termsReview(termsReview);

        Assertions.assertNotNull(responseTermsReview);
        Assertions.assertFalse(responseTermsReview.getTermsRequired());
    }

    @Test
    public void clusterAuthorization() {
        final ClusterAuthorization clusterAuthorization = ClusterAuthorization.builder()
                .accountUsername("testUser.openshift")
                .productId("rhosr")
                .managed(true)
                .byoc(false)
                .clusterId("foobar")
                .cloudProviderId("aws")
                .reserve(true)
                .availabilityZone("single")
                .resources(Collections.singletonList(ReservedResource.builder().resourceType("cluster.aws").resourceName("rhosr").count(1).build()))
                .build();

        final ClusterAuthorizationResponse clusterAuthorizationResponse = accountManagementSystemRestClient.clusterAuthorization(clusterAuthorization);

        Assertions.assertNotNull(clusterAuthorizationResponse);
        Assertions.assertTrue(clusterAuthorizationResponse.getAllowed());
        Assertions.assertNotNull(clusterAuthorizationResponse.getSubscription().getId());
    }

    @Test
    public void deleteSubscription() {
        accountManagementSystemRestClient.deleteSubscription("1vJoDH2CWtf8ix7YDPt1fGWGBSg");
    }

    @AfterAll
    public static void stopServer() {
        amsWireMockServer.stop();
    }
}
