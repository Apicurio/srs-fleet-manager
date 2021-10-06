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

package org.bf2.srs.fleetmanager.rest.service;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

/**
 * @author Fabian Martinez
 */
@QuarkusTest
@TestProfile(StaticRegistryDeploymentsTestProfile.class)
public class StaticRegistryDeploymentsTest {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/admin/registryDeployments";

    @Test
    public void testDeploymentsRegistered() {
        var deployments = given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(200)
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                }).stream().collect(Collectors.toMap(d -> d.getName(), d -> d));

        assertTrue(deployments.size() == 2);

        var dep1 = deployments.get("deployment-1");
        assertNotNull(dep1);
        assertEquals("http://test-registry:8080", dep1.getRegistryDeploymentUrl());
        assertEquals("http://tenant-manager-test:8585", dep1.getTenantManagerUrl());

        var dep2 = deployments.get("deployment-2");
        assertNotNull(dep2);
        assertEquals("http://test-registry-2:8080", dep2.getRegistryDeploymentUrl());
        assertEquals("http://tenant-manager-test-2:8585", dep2.getTenantManagerUrl());
    }

    @Test
    public void testRestEndpointsDisabled() {

        var valid1 = new RegistryDeploymentCreateRest();
        valid1.setName("a");
        valid1.setTenantManagerUrl("https://aaaa:443");
        valid1.setRegistryDeploymentUrl("https://foo:8443");

        given()
            .log().all()
            .when().contentType(ContentType.JSON).body(valid1).post(BASE)
            .then()
            .statusCode(HTTP_FORBIDDEN);

        var deployment1Id = given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(200)
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                }).get(0).getId();

        given()
            .log().all()
            .when().delete(BASE + "/" + deployment1Id)
            .then().statusCode(HTTP_FORBIDDEN);

    }

}
