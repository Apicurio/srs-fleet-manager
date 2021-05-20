package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentRest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
class RegistryDeploymentsResourceV1Test {

    private static final String BASE = "/api/v1/admin/registryDeployments";

    @Test
    void testCreateRegistryDeployment() {
        var valid1 = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        var valid2 = RegistryDeploymentCreateRest.builder()
                .tenantManagerUrl("b")
                .registryDeploymentUrl("b")
                .build();

        var invalid1 = RegistryDeploymentCreateRest.builder()
                .registryDeploymentUrl("c")
                .tenantManagerUrl("")
                .build();

        var invalid2 = RegistryDeploymentCreateRest.builder()
                .registryDeploymentUrl("d")
                .build();

        var invalid3 = RegistryDeploymentCreateRest.builder()
                .registryDeploymentUrl("")
                .tenantManagerUrl("e")
                .build();

        var invalid4 = RegistryDeploymentCreateRest.builder()
                .tenantManagerUrl("f")
                .build();

        var invalidJson1 = "{\"invalid\": true}";

        var invalidJson2 = "invalid";

        // Error 415
        given()
                .when().body(valid1).post(BASE)
                .then().statusCode(HTTP_UNSUPPORTED_TYPE)
                .log().all();

        // Error 400
        List.of(invalid1, invalid2, invalid3, invalid4, invalidJson1, invalidJson2).forEach(d -> {
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
                    .extract().as(RegistryDeploymentRest.class).getId();
        }).collect(toList());

        // Error 409
        given()
                .when().contentType(ContentType.JSON).body(valid1).post(BASE)
                .then().statusCode(HTTP_CONFLICT)
                .log().all();

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });
    }

    @Test
    void testGetRegistryDeployments() {

        given()
                .when().get(BASE)
                .then().statusCode(HTTP_OK).body("", equalTo(JsonPath.from("[]").getList("")))
                .log().all();

        var valid1 = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        var valid2 = RegistryDeploymentCreateRest.builder()
                .tenantManagerUrl("b")
                .registryDeploymentUrl("b")
                .build();

        // Create
        List<Long> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_ACCEPTED)
                    .log().all()
                    .extract().as(RegistryDeploymentRest.class).getId();
        }).collect(toList());

        List<Long> actualIds = given()
                .when().get(BASE)
                .then().statusCode(200)
                .log().all()
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                }).stream().map(RegistryDeploymentRest::getId).collect(toList());

        assertThat(actualIds, containsInAnyOrder(ids.toArray()));

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });
    }

    @Test
    void testGetRegistryDeployment() {
        // Error 404
        given()
                .when().get(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND).body("error_code", equalTo(404))
                .log().all();

        var valid1 = RegistryDeploymentCreateRest.builder()
                .name("a")
                .tenantManagerUrl("a")
                .registryDeploymentUrl("a")
                .build();

        var valid2 = RegistryDeploymentCreateRest.builder()
                .tenantManagerUrl("b")
                .registryDeploymentUrl("b")
                .build();

        // Create
        List<Long> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_ACCEPTED)
                    .log().all()
                    .extract().as(RegistryDeploymentRest.class).getId();
        }).collect(toList());

        ids.forEach(id -> {
            given()
                    .when().get(BASE + "/" + id)
                    // NOTE: Test framework assumes that JSON number is `int` instead of `long`.
                    .then().statusCode(HTTP_OK).body("id", equalTo(id.intValue()))
                    .log().all();
        });

        // Delete
        ids.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });
    }

    @Test
    void testDeleteRegistryDeployment() {

        given()
                .when().delete(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND)
                .log().all();
    }
}
