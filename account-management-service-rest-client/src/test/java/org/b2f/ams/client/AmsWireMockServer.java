package org.b2f.ams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class AmsWireMockServer {

    private WireMockServer wireMockServer;

    public String start() throws JsonProcessingException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        stubFor(post(urlEqualTo("/api/accounts_mgmt/v1/cluster_authorizations"))
                .withRequestBody(equalToJson("{\"account_username\":\"testUser.openshift\"," +
                        " \"product_id\":\"rhosr\", " +
                        "\"managed\":true, " +
                        "\"byoc\":false, " +
                        "\"cluster_id\":\"foobar\", " +
                        "\"cloud_provider_id\":\"aws\", " +
                        "\"reserve\":true, " +
                        "\"availability_zone\":\"single\", " +
                        "\"resources\":[{\"resource_type\":\"cluster.aws\", \"resource_name\":\"rhosr\", \"count\":1}]}")
                )
                .willReturn(aResponse()
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

        stubFor(delete(urlEqualTo("/api/accounts_mgmt/v1/subscriptions/1vJoDH2CWtf8ix7YDPt1fGWGBSg"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"id\": \"1vJoDH2CWtf8ix7YDPt1fGWGBSg\"}"
                        )));

        stubFor(post(urlEqualTo("/api/authorizations/v1/terms_review"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"account_username\": \"test.account.username\"}")
                )
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"account_id\": \"test.account.username\",\n" +
                                        "\"organization_id\": \"org_id\",\n" +
                                        "\"redirect_url\": \"string\",\n" +
                                        "\"terms_available\": true,\n" +
                                        "\"terms_required\": false\n" +
                                        "" + "}"
                        )));

        return wireMockServer.baseUrl();
    }

    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
