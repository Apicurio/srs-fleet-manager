package org.bf2.srs.fleetmanager.it.ams;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class AmsWireMockServer {

    private WireMockServer wireMockServer;

    public String start() throws JsonProcessingException {
        wireMockServer = new WireMockServer(
                wireMockConfig()
                        .dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/accounts_mgmt/v1/cluster_authorizations"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\n" +
                                        "  \"allowed\": true,\n" +
                                        "  \"excess_resources\": [],\n" +
                                        "  \"subscription\": {\n" +
                                        "    \"id\": \"1vJoDH2CWtf8ix7YDPt1fGWGBSg\",\n" +
                                        "    \"kind\": \"Subscription\",\n" +
                                        "    \"href\": \"/api/accounts_mgmt/v1/subscriptions/1vJoDH2CWtf8ix7YDPt1fGWGBSg\"\n" +
                                        "  },\n" +
                                        "  \"organization_id\": \"1vJmntalGLfL1gVZ2m5Z1C9MYzs\"\n" +
                                        "}"
                        )));

        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/api/accounts_mgmt/v1/subscriptions/1vJoDH2CWtf8ix7YDPt1fGWGBSg"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"id\": \"1vJoDH2CWtf8ix7YDPt1fGWGBSg\"}"
                        )));

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/authorizations/v1/terms_review"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"account_id\": \"test.account.username\",\n" +
                                        "\"organization_id\": \"org_id\",\n" +
                                        "\"redirect_url\": \"string\",\n" +
                                        "\"terms_available\": true,\n" +
                                        "\"terms_required\": false\n" +
                                        "" + "}"
                        )));

        // Mock response for searching for an organization by its external org id
        WireMock.stubFor(WireMock.get(WireMock.urlMatching("/api/accounts_mgmt/v1/organizations\\?search.+"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\r\n" +
                                "    \"kind\": \"OrganizationList\",\r\n" +
                                "    \"page\": 1,\r\n" +
                                "    \"size\": 1,\r\n" +
                                "    \"total\": 1,\r\n" +
                                "    \"items\": [\r\n" +
                                "        {\r\n" +
                                "        \"id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "        \"kind\" : \"Organization\",\r\n" +
                                "        \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "        \"name\" : \"Red Hat\",\r\n" +
                                "        \"external_id\" : \"14221005\",\r\n" +
                                "        \"ebs_account_id\" : \"7072918\"\r\n" +
                                "        }\r\n" +
                                "    ]\r\n" +
                                "}"
                        )));


        // Mock response for getting a list of QuotaCost objects for an org
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost?fetchRelatedResources=true"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\r\n" +
                                "  \"kind\" : \"QuotaCostList\",\r\n" +
                                "  \"page\" : 1,\r\n" +
                                "  \"size\" : 3,\r\n" +
                                "  \"total\" : 3,\r\n" +
                                "  \"items\" : [ {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"add-on|addon-cluster-logging-operator\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"addon-cluster-logging-operator\",\r\n" +
                                "      \"resource_type\" : \"add-on\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"ANY\",\r\n" +
                                "      \"billing_model\" : \"any\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"cluster|rhinfra|rhosr|any\",\r\n" +
                                "    \"allowed\" : 3,\r\n" +
                                "    \"consumed\" : 1,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"rhosr\",\r\n" +
                                "      \"resource_type\" : \"cluster\",\r\n" +
                                "      \"byoc\" : \"rhinfra\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"RHOSR\",\r\n" +
                                "      \"billing_model\" : \"any\",\r\n" +
                                "      \"cost\" : 1\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"network.io|network-io-latam|aws\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"network-io-latam\",\r\n" +
                                "      \"resource_type\" : \"network.io\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"ANY\",\r\n" +
                                "      \"billing_model\" : \"any\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  } ]\r\n" +
                                "}"
                        )));

        return wireMockServer.baseUrl();
    }

    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
