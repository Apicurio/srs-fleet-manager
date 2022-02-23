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

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.junit.jupiter.api.Test;

/**
 * @author Fabian Martinez
 */
public class MetricsIT extends SRSFleetManagerBaseIT {

    private final List<String> expectedMetrics = List.of(
             "srs_fleet_manager_auth_seconds_max",
             "srs_fleet_manager_auth_seconds_count",
             "srs_fleet_manager_auth_seconds_sum",

            // "srs_fleet_manager_ams_client_errors",

            "srs_fleet_manager_ams_determine_allowed",
            "srs_fleet_manager_ams_create",
             "srs_fleet_manager_ams_delete",

            "srs_fleet_manager_tm_create",
             "srs_fleet_manager_tm_delete",

            "srs_fleet_manager_usage_users",
            "srs_fleet_manager_usage_registries_type",
            "srs_fleet_manager_usage_organisations",
            "srs_fleet_manager_usage_registries_status",

            "rest_requests_count_total",
            "rest_requests_seconds_max",
            "rest_requests_seconds"
        );


    @Test
    void testMetricsCreated() {

        var alice = new AccountInfo("testMetricsCreated", "alice", false, 10L);

        var registry1 = new RegistryCreate();
        registry1.setName("test-metrics");

        var registry1Result = FleetManagerApi.createRegistry(registry1, alice);

        FleetManagerApi.waitRegistryReady(registry1Result, alice);

        // Delete
        FleetManagerApi.deleteRegistry(registry1Result.getId(), alice);

        FleetManagerApi.waitRegistryDeleted(registry1Result, alice);

        var metrics = given()
            .when()
                .get("/q/metrics")
            .then().statusCode(HTTP_OK)
                .log().all()
                .extract().asString();

        assertTrue(metrics.contains("srs_fleet_manager"), "Metrics don't contain any custom metric");

        for (String m : expectedMetrics) {
            assertTrue(metrics.contains(m), () ->  "Missing metric " + m);
        }

    }

}
