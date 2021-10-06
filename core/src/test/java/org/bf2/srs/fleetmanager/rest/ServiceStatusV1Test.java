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

package org.bf2.srs.fleetmanager.rest;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.ServiceStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * @author Fabian Martinez
 */
@QuarkusTest
public class ServiceStatusV1Test {

    public static final String BASE = "/api/serviceregistry_mgmt/v1/status";

    @Test
    public void testServiceStatus() {

        var res1 = given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(HTTP_OK)
                .extract().as(ServiceStatus.class);

        assertFalse(res1.getMaxInstancesReached());


    }

}
