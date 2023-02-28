package org.bf2.srs.fleetmanager.ams.client;

import java.util.Collections;

import org.bf2.srs.fleetmanager.spi.ams.impl.remote.AccountManagementSystemRestClient;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.ClusterAuthorization;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.ReservedResource;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.request.TermsReview;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.ClusterAuthorizationResponse;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.Organization;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.QuotaCostList;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.model.response.ResponseTermsReview;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    @Test
    public void getOrganizationByExternalId() {
        final Organization org = accountManagementSystemRestClient.getOrganizationByExternalId("12345");

        Assertions.assertNotNull(org);
        Assertions.assertEquals("1pcZDw72EPhdanw4pJEnrudOnyj", org.getId());
    }

    @Test
    public void getQuotaCostList() {
        final QuotaCostList quotaCostList = accountManagementSystemRestClient.getQuotaCostList("1pcZDw72EPhdanw4pJEnrudOnyj", true);

        Assertions.assertNotNull(quotaCostList);
        Assertions.assertEquals(20, quotaCostList.getTotal());
        Assertions.assertNotNull(quotaCostList.getItems());
        Assertions.assertFalse(quotaCostList.getItems().isEmpty());
        Assertions.assertEquals(20, quotaCostList.getItems().size());
        Assertions.assertEquals("add-on|addon-cluster-logging-operator", quotaCostList.getItems().get(0).getQuota_id());
    }

    @AfterAll
    public static void stopServer() {
        amsWireMockServer.stop();
    }
}
