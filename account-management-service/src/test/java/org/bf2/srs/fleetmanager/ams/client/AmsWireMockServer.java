package org.bf2.srs.fleetmanager.ams.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class AmsWireMockServer {

    private WireMockServer wireMockServer;

    public String start() throws JsonProcessingException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        // Mock response for consuming quota for Standard instance
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/accounts_mgmt/v1/cluster_authorizations"))
                .withRequestBody(WireMock.equalToJson("{\"account_username\":\"testUser.openshift\"," +
                        " \"product_id\":\"rhosr\", " +
                        "\"managed\":true, " +
                        "\"byoc\":false, " +
                        "\"cluster_id\":\"foobar\", " +
                        "\"cloud_provider_id\":\"aws\", " +
                        "\"reserve\":true, " +
                        "\"availability_zone\":\"single\", " +
                        "\"resources\":[{\"resource_type\":\"cluster.aws\", \"resource_name\":\"rhosr\", \"count\":1}]}")
                )
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

        // Mock response for deleting a subscription
        WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo("/api/accounts_mgmt/v1/subscriptions/1vJoDH2CWtf8ix7YDPt1fGWGBSg"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"id\": \"1vJoDH2CWtf8ix7YDPt1fGWGBSg\"}"
                        )));

        // Mock response for checking the terms & conditions.
        WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/authorizations/v1/terms_review"))
                .withRequestBody(WireMock.equalToJson("{\n" +
                        "  \"account_username\": \"test.account.username\"}")
                )
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
                                "  \"size\" : 20,\r\n" +
                                "  \"total\" : 20,\r\n" +
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
                                "    \"quota_id\" : \"compute.node|cpu|byoc|osd\",\r\n" +
                                "    \"allowed\" : 128,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-16\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.large\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-8\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.medium\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-36\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 36\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.xlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 36\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-48\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.xxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-72\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 72\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.xxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 72\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highcpu-96\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"cpu.xxxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-16\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.large\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-8\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.medium\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-4\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 4\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.small\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 4\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-32\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 32\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.xlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 32\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-48\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.xxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-64\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 64\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.xxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 64\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"standard-96\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp.xxxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-16\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.large\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 16\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-8\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.medium\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 8\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-4\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 4\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.small\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 4\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-32\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 32\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.xlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 32\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-48\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.xxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 48\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-64\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 64\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.xxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 64\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"highmem-96\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"mem.xxxxlarge\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 96\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"cluster|cpu|byoc|ocp\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"any\",\r\n" +
                                "      \"resource_type\" : \"cluster\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OCP\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"compute.node|cpu|byoc|ocp\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"any\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OCP\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"cluster|byoc|moa\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"any\",\r\n" +
                                "      \"resource_type\" : \"cluster\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"MOA\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"compute.node|cpu|byoc|moa\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"highcpu-16\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"MOA\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    }, {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"cpu.large\",\r\n" +
                                "      \"resource_type\" : \"compute.node\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"MOA\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"cluster|rhinfra|rhosr|any\",\r\n" +
                                "    \"allowed\" : 3,\r\n" +
                                "    \"consumed\" : 2,\r\n" +
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
                                "    \"quota_id\" : \"network.io|network-io-apac|aws\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"network-io-apac\",\r\n" +
                                "      \"resource_type\" : \"network.io\",\r\n" +
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
                                "    \"quota_id\" : \"add-on|addon-managed-api-service\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"addon-managed-api-service\",\r\n" +
                                "      \"resource_type\" : \"add-on\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSDTrial\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"network.io|network-io-north-america|aws\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"network-io-north-america\",\r\n" +
                                "      \"resource_type\" : \"network.io\",\r\n" +
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
                                "    \"quota_id\" : \"add-on|addon-ocm-test-operator\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"addon-ocm-test-operator\",\r\n" +
                                "      \"resource_type\" : \"add-on\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"ANY\",\r\n" +
                                "      \"billing_model\" : \"any\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"add-on|addon-rhmi-operator\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"addon-rhmi-operator\",\r\n" +
                                "      \"resource_type\" : \"add-on\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"RHMI\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"network.io|network-io|gcp\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"gcp\",\r\n" +
                                "      \"resource_name\" : \"network-io\",\r\n" +
                                "      \"resource_type\" : \"network.io\",\r\n" +
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
                                "    \"quota_id\" : \"network.loadbalancer|network\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"network\",\r\n" +
                                "      \"resource_type\" : \"network.loadbalancer\",\r\n" +
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
                                "    \"quota_id\" : \"cluster|rhinfra|rhosaktrial|marketplace\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"rhosak\",\r\n" +
                                "      \"resource_type\" : \"cluster\",\r\n" +
                                "      \"byoc\" : \"rhinfra\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"RHOSAKTrial\",\r\n" +
                                "      \"billing_model\" : \"marketplace\",\r\n" +
                                "      \"cost\" : 0\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"cluster|byoc|osd\",\r\n" +
                                "    \"allowed\" : 8,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"any\",\r\n" +
                                "      \"resource_type\" : \"cluster\",\r\n" +
                                "      \"byoc\" : \"byoc\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"OSD\",\r\n" +
                                "      \"billing_model\" : \"standard\",\r\n" +
                                "      \"cost\" : 1\r\n" +
                                "    } ]\r\n" +
                                "  }, {\r\n" +
                                "    \"kind\" : \"QuotaCost\",\r\n" +
                                "    \"href\" : \"/api/accounts_mgmt/v1/organizations/1pcZDw72EPhdanw4pJEnrudOnyj/quota_cost\",\r\n" +
                                "    \"organization_id\" : \"1pcZDw72EPhdanw4pJEnrudOnyj\",\r\n" +
                                "    \"quota_id\" : \"pv.storage|gp2\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"gp2\",\r\n" +
                                "      \"resource_type\" : \"pv.storage\",\r\n" +
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
                                "    \"quota_id\" : \"network.io|network-io-emea|aws\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"aws\",\r\n" +
                                "      \"resource_name\" : \"network-io-emea\",\r\n" +
                                "      \"resource_type\" : \"network.io\",\r\n" +
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
                                "    \"quota_id\" : \"add-on|addon-crw-operator\",\r\n" +
                                "    \"allowed\" : 0,\r\n" +
                                "    \"consumed\" : 0,\r\n" +
                                "    \"related_resources\" : [ {\r\n" +
                                "      \"cloud_provider\" : \"any\",\r\n" +
                                "      \"resource_name\" : \"addon-crw-operator\",\r\n" +
                                "      \"resource_type\" : \"add-on\",\r\n" +
                                "      \"byoc\" : \"any\",\r\n" +
                                "      \"availability_zone_type\" : \"any\",\r\n" +
                                "      \"product\" : \"ANY\",\r\n" +
                                "      \"billing_model\" : \"any\",\r\n" +
                                "      \"cost\" : 0\r\n" +
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
