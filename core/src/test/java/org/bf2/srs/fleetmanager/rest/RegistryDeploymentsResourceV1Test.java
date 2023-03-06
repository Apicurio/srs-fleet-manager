package org.bf2.srs.fleetmanager.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
class RegistryDeploymentsResourceV1Test {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/admin/registryDeployments";

    @BeforeEach
    void cleanup() {
        List<Integer> actualIds = given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(200)
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                }).stream().map(RegistryDeploymentRest::getId).collect(toList());

        // Delete
        actualIds.forEach(id -> {
            given()
                    .log().all()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT);
        });
    }

    @Test
    void testCreateRegistryDeployment() {
        var valid1 = new RegistryDeploymentCreateRest();
        valid1.setName(UUID.randomUUID().toString());
        valid1.setTenantManagerUrl("https://aaaa:443");
        valid1.setRegistryDeploymentUrl("https://fooregistry");

        var valid2 = new RegistryDeploymentCreateRest();
        valid2.setName(UUID.randomUUID().toString());
        valid2.setTenantManagerUrl("https://bbbb:443");
        valid2.setRegistryDeploymentUrl("https://bazregistry");

        var valid3 = new RegistryDeploymentCreateRest();
        valid3.setName(UUID.randomUUID().toString());
        valid3.setTenantManagerUrl("https://ccc:443");
        valid3.setRegistryDeploymentUrl("https://foo:8443");

        var valid4 = new RegistryDeploymentCreateRest();
        valid4.setName(UUID.randomUUID().toString());
        valid4.setTenantManagerUrl("https://aaaa:443/api");
        valid4.setRegistryDeploymentUrl("https://foo:8443/foo");

        var invalid1 = new RegistryDeploymentCreateRest();
        invalid1.setRegistryDeploymentUrl("c");
        invalid1.setTenantManagerUrl("");

        var invalid2 = new RegistryDeploymentCreateRest();
        invalid2.setRegistryDeploymentUrl("d");

        var invalid3 = new RegistryDeploymentCreateRest();
        invalid3.setRegistryDeploymentUrl("");
        invalid3.setTenantManagerUrl("e");

        var invalid4 = new RegistryDeploymentCreateRest();
        invalid4.setTenantManagerUrl("f");

        var invalid5 = new RegistryDeploymentCreateRest();
        invalid5.setName(UUID.randomUUID().toString());
        invalid5.setTenantManagerUrl("htttttppsss/api");
        invalid5.setRegistryDeploymentUrl("ht:8443/foo");

        var invalidJson1 = "{\"invalid\": true}";

        var invalidJson2 = "invalid";

        // Error 415
        given()
                .log().all()
                .when().body(valid1).post(BASE)
                .then().statusCode(HTTP_UNSUPPORTED_TYPE);

        // Error 400
        List.of(invalid1, invalid2, invalid3, invalid4, invalid5, invalidJson1, invalidJson2).forEach(d -> {
            given()
                    .log().all()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_BAD_REQUEST);
        });

        List<Integer> ids = List.of(valid1, valid2, valid3, valid4).stream().map(d -> {
            return given()
                    .log().all()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then()
                    .statusCode(HTTP_OK)
                    .extract().as(RegistryDeploymentRest.class).getId();
        }).collect(toList());

        // Error 409
        given()
                .log().all()
                .when().contentType(ContentType.JSON).body(valid1).post(BASE)
                .then().statusCode(HTTP_CONFLICT);

        // Delete
        ids.forEach(id -> {
            given()
                    .log().all()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT);
        });
    }

    @Test
    void testGetRegistryDeployments() {

        given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(HTTP_OK).body("", equalTo(JsonPath.from("[]").getList("")));

        var valid1 = new RegistryDeploymentCreateRest();
        valid1.setName("a");
        valid1.setTenantManagerUrl("https://aaaa:443");
        valid1.setRegistryDeploymentUrl("https://fooregistry");

        var valid2 = new RegistryDeploymentCreateRest();
        valid2.setName("b");
        valid2.setTenantManagerUrl("https://bbbb:443");
        valid2.setRegistryDeploymentUrl("https://bazregistry");

        // Create
        List<Integer> ids = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .log().all()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .extract().as(RegistryDeploymentRest.class).getId();
        }).collect(toList());

        List<Integer> actualIds = given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(200)
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                }).stream().map(RegistryDeploymentRest::getId).collect(toList());

        assertThat(actualIds, containsInAnyOrder(ids.toArray()));

        // Delete
        ids.forEach(id -> {
            given()
                    .log().all()
                    .when().delete(BASE + "/" + id)
                    .then().statusCode(HTTP_NO_CONTENT);
        });
    }

    @Test
    void testGetRegistryDeployment() {
        // Error 404
        given()
                .log().all()
                .when().get(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND).body("error_code", equalTo(404));

        var valid1 = new RegistryDeploymentCreateRest();
        valid1.setName("a");
        valid1.setTenantManagerUrl("https://aaaa:443");
        valid1.setRegistryDeploymentUrl("https://fooregistry");

        var valid2 = new RegistryDeploymentCreateRest();
        valid2.setName("b");
        valid2.setTenantManagerUrl("https://bbbb:443");
        valid2.setRegistryDeploymentUrl("https://bazregistry");

        // Create
        List<RegistryDeploymentRest> rds = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .log().all()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .extract().as(RegistryDeploymentRest.class);
        }).collect(toList());

        rds.forEach(rd -> {
            var res = given()
                    .log().all()
                    .when().get(BASE + "/" + rd.getId())
                    // NOTE: Test framework assumes that JSON number is `int` instead of `long`.
                    .then().statusCode(HTTP_OK);

            var json = res.extract().as(JsonNode.class);
            assertTrue(json.get("status").get("lastUpdated").asText().endsWith("Z"));

            var apiRd = res.extract().as(RegistryDeploymentRest.class);
            assertEquals(rd.getId(), apiRd.getId());
        });

        // Delete
        rds.forEach(rd -> {
            given()
                    .log().all()
                    .when().delete(BASE + "/" + rd.getId())
                    .then().statusCode(HTTP_NO_CONTENT);
        });
    }

    @Test
    void testDeleteRegistryDeployment() {

        given()
                .log().all()
                .when().delete(BASE + "/1000")
                .then().statusCode(HTTP_NOT_FOUND);
    }

}
