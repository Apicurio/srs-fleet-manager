/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.it.jwks;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;

/**
 * @author Fabian Martinez
 */
public class JWKSMockServer {

    static final Logger LOGGER = LoggerFactory.getLogger(JWKSMockServer.class);

    private WireMockServer server;

    private String realm = "test";

    public String start() {

        server = new WireMockServer(
                wireMockConfig()
                        .dynamicPort());
        server.start();

        server.stubFor(
                get(urlEqualTo("/auth/realms/" + realm + "/.well-known/openid-configuration"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "    \"jwks_uri\": \"" + server.baseUrl()
                                        + "/auth/realms/" + realm + "/protocol/openid-connect/certs\""
                                        + "}")));

        server.stubFor(
                get(urlEqualTo("/auth/realms/" + realm + "/protocol/openid-connect/certs"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\n" +
                                        "  \"keys\" : [\n" +
                                        "    {\n" +
                                        "      \"kid\": \"1\",\n" +
                                        "      \"kty\":\"RSA\",\n" +
                                        "      \"n\":\"iJw33l1eVAsGoRlSyo-FCimeOc-AaZbzQ2iESA3Nkuo3TFb1zIkmt0kzlnWVGt48dkaIl13Vdefh9hqw_r9yNF8xZqX1fp0PnCWc5M_TX_ht5fm9y0TpbiVmsjeRMWZn4jr3DsFouxQ9aBXUJiu26V0vd2vrECeeAreFT4mtoHY13D2WVeJvboc5mEJcp50JNhxRCJ5UkY8jR_wfUk2Tzz4-fAj5xQaBccXnqJMu_1C6MjoCEiB7G1d13bVPReIeAGRKVJIF6ogoCN8JbrOhc_48lT4uyjbgnd24beatuKWodmWYhactFobRGYo5551cgMe8BoxpVQ4to30cGA0qjQ\",\n"
                                        +
                                        "      \"e\":\"AQAB\"\n" +
                                        "    }\n" +
                                        "  ]\n" +
                                        "}")));

        LOGGER.info("Keycloak started in mock mode: %s", server.baseUrl());

        return server.baseUrl();
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop();
            LOGGER.info("Keycloak was shut down");
            server = null;
        }
    }


}
