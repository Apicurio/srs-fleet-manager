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

package org.bf2.srs.fleetmanager.it;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian Martinez
 */
public class HttpUtils {

    static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Generic check if an endpoint is network reachable
     * @param host
     * @param port
     * @param component
     * @return true if it's possible to open a network connection to the endpoint
     */
    public static boolean isReachable(String host, int port, String component) {
        try (Socket socket = new Socket()) {
            log.info("Trying to connect to {}:{}", host, port);
            socket.connect(new InetSocketAddress(host, port), 5_000);
            log.info("Client is able to connect to " + component);
            return  true;
        } catch (IOException ex) {
            log.warn("Cannot connect to {}: {}", component, ex.getMessage());
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }


    /**
     * Generic check of the /health/ready endpoint
     *
     * @param baseUrl
     * @param logResponse
     * @param component
     * @return true if the readiness endpoint replies successfully
     */
    public static boolean isReady(String baseUrl, String healthUrl, boolean logResponse, String component) {
        try {
            CloseableHttpResponse res = HttpClients.createMinimal().execute(new HttpGet(baseUrl.concat(healthUrl)));
            boolean ok = res.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            if (ok) {
                log.info(component + " is ready");
            }
            if (logResponse) {
                log.info(IOUtils.toString(res.getEntity().getContent(), StandardCharsets.UTF_8));
            }
            return ok;
        } catch (IOException e) {
            log.error(component + " is not ready {}", e.getMessage());
            return false;
        }
    }


}
