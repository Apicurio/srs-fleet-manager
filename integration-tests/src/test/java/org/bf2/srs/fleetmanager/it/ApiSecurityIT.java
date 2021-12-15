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
import org.junit.jupiter.api.Test;

/**
 * @author Fabian Martinez
 */
public class ApiSecurityTest extends SRSFleetManagerBaseIT {

    @Test
    void testOpenEndpoints() {

        FleetManagerApi.verifyApiIsSecured();

        given()
            .log().all()
            .when().get("/api/serviceregistry_mgmt/v1/errors")
            .then().statusCode(HTTP_OK);

        given()
            .log().all()
            .when().get("/api/serviceregistry_mgmt/v1/errors/1")
            .then().statusCode(HTTP_OK);

        given()
            .log().all()
            .when().get("/api/serviceregistry_mgmt/v1/openapi")
            .then().statusCode(HTTP_OK);

    }
}
