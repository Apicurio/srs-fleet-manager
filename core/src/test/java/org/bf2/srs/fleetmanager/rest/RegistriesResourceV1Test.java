package org.bf2.srs.fleetmanager.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.util.TestUtil.delay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class RegistriesResourceV1Test {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    @Test
    void testCreateRegistry() {
        var deployment = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        Long deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_ACCEPTED)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = RegistryCreateRest.builder()
                .name("a")
                .build();

        var valid2 = RegistryCreateRest.builder()
                .build();

        var invalidJson1 = "{\"invalid\": true}";

        var invalidJson2 = "invalid";

        // Error 415
        given()
                .when().body(valid1).post(BASE)
                .then().statusCode(HTTP_UNSUPPORTED_TYPE)
                .log().all();

        // Error 400
        List.of(invalidJson1, invalidJson2).forEach(d -> {
            given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_BAD_REQUEST)
                    .log().all();
        });

        List<Long> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_ACCEPTED)
                    .log().all()
                    .extract().as(RegistryRest.class).getId();
        }).collect(toList());

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        given()
                .when().contentType(ContentType.JSON).delete("/api/serviceregistry_mgmt/v1/admin/registryDeployments/" + deploymentId)
                .then().statusCode(HTTP_NO_CONTENT)
                .log().all();
    }

    @Test
    void testGetRegistries() {
        given()
                .when().get(BASE)
                .then().statusCode(HTTP_OK).body("", equalTo(JsonPath.from("[]").getList("")))
                .log().all();

        var deployment = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        Long deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_ACCEPTED)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = RegistryCreateRest.builder()
                .name("a")
                .build();

        var valid2 = RegistryCreateRest.builder()
                .build();

        // Create
        List<Long> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_ACCEPTED)
                    .log().all()
                    .extract().as(RegistryRest.class).getId();
        }).collect(toList());

        List<Long> actualIds = given()
                .when().get(BASE)
                .then().statusCode(200)
                .log().all()
                .extract().as(new TypeRef<List<RegistryRest>>() {
                }).stream().map(RegistryRest::getId).collect(toList());

        assertThat(actualIds, containsInAnyOrder(ids.toArray()));

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        given()
                .when().contentType(ContentType.JSON).delete("/api/serviceregistry_mgmt/v1/admin/registryDeployments/" + deploymentId)
                .then().statusCode(HTTP_NO_CONTENT)
                .log().all();
    }

    @Test
    void testGetRegistry() {
        // Error 404
        given()
                .when().get(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND).body("error_code", equalTo(404))
                .log().all();

        var deployment = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        Long deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_ACCEPTED)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = RegistryCreateRest.builder()
                .name("a")
                .build();

        var valid2 = RegistryCreateRest.builder()
                .build();

        // Create
        List<Long> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_ACCEPTED)
                    .log().all()
                    .extract().as(RegistryRest.class).getId();
        }).collect(toList());

        delay(3 * 1000);

        ids.forEach(id -> {
            given()
                    .when().get(BASE + "/" + id)
                    .then().statusCode(HTTP_OK)
                    // NOTE: Test framework assumes that JSON number is `int` instead of `long`.
                    .body("id", equalTo(id.intValue()))
                    .body("registryDeploymentId", equalTo(deploymentId.intValue()))
                    .log().all();
        });

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        given()
                .when().contentType(ContentType.JSON).delete("/api/serviceregistry_mgmt/v1/admin/registryDeployments/" + deploymentId)
                .then().statusCode(HTTP_NO_CONTENT)
                .log().all();
    }

    @Test
    void testDeleteRegistry() {

        given()
                .when().delete(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND)
                .log().all();
    }
}