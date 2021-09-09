package org.bf2.srs.fleetmanager.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.util.TestUtil.delay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class RegistriesResourceV1Test {

    public static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    @Inject
    TenantManagerService tms;

    @Test
    void testCreateRegistry() {
        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("a");
        deployment.setTenantManagerUrl("https://tenant-manager");
        deployment.setRegistryDeploymentUrl("https://registry");

        Integer deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = new RegistryCreate();
        valid1.setName("a");

        var invalidRepeatedName = new RegistryCreate();
        valid1.setName("a");

        var valid2 = new RegistryCreate();
        valid2.setName("foosafasdfasf");

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

        List<Registry> registries = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(Registry.class);
        }).collect(toList());

        given()
                .when().contentType(ContentType.JSON).body(invalidRepeatedName).post(BASE)
                .then().statusCode(HTTP_CONFLICT);

        registries = TestUtil.waitForReady(registries);

        // Delete
        registries.forEach(id -> {
            given()
                    .when().delete(BASE + "/" + id.getId())
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        TestUtil.waitForDeletion(tms, TenantManagerConfig.builder()
                        .tenantManagerUrl(deployment.getTenantManagerUrl())
                        .registryDeploymentUrl(deployment.getRegistryDeploymentUrl()).build(),
                registries);

        given()
                .when().contentType(ContentType.JSON).delete("/api/serviceregistry_mgmt/v1/admin/registryDeployments/" + deploymentId)
                .then().statusCode(HTTP_NO_CONTENT)
                .log().all();
    }

    @Test
    void testGetRegistries() {
        var res1 = given()
                .when().get(BASE)
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryList.class);

        assertThat(res1.getItems(), equalTo(List.of()));
        assertThat(res1.getPage(), equalTo(1));
        assertThat(res1.getSize(), equalTo(10));
        assertThat(res1.getTotal(), equalTo(0));

        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("a");
        deployment.setTenantManagerUrl("https://tenant-manager");
        deployment.setRegistryDeploymentUrl("https://registry");

        Integer deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = new RegistryCreate();
        valid1.setName("a");

        var valid2 = new RegistryCreate();
        valid2.setName("bbbb");

        // Create
        List<Registry> registries = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(Registry.class);
        }).collect(toList());

        List<Registry> actualRegistries = given()
                .when().get(BASE)
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryList.class)
                .getItems();

        assertThat(actualRegistries.stream().map(Registry::getId).collect(toList()),
                containsInAnyOrder(registries.stream().map(Registry::getId).toArray()));

        registries = TestUtil.waitForReady(registries);

        // Delete
        registries.forEach(r -> {
            given()
                    .when().delete(BASE + "/" + r.getId())
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        TestUtil.waitForDeletion(tms, TenantManagerConfig.builder()
                        .tenantManagerUrl(deployment.getTenantManagerUrl())
                        .registryDeploymentUrl(deployment.getRegistryDeploymentUrl()).build(),
                registries);

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
                .then().statusCode(HTTP_NOT_FOUND).body("code", equalTo("SRS-MGMT-2"))// TODO
                .log().all();

        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("a");
        deployment.setTenantManagerUrl("https://tenant-manager");
        deployment.setRegistryDeploymentUrl("https://registry");

        Integer deploymentId = given()
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryDeploymentRest.class).getId();

        var valid1 = new RegistryCreate();
        valid1.setName("a");
        valid1.setDescription("foo");

        var valid2 = new RegistryCreate();
        valid2.setName("bbb");
        valid2.setDescription("hello world");

        // Create
        List<Registry> regs = List.of(valid1, valid2).stream().map(d -> {
            return given()
                    .when().contentType(ContentType.JSON).body(d).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(Registry.class);
        }).collect(toList());

        delay(3 * 1000);

        regs.forEach(reg -> {

            var apiReg = given()
                    .when().get(BASE + "/" + reg.getId())
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(Registry.class);
            assertNotNull(apiReg.getName());
            assertEquals(reg.getName(), apiReg.getName());
            assertNotNull(apiReg.getDescription());
            assertEquals(reg.getDescription(), apiReg.getDescription());
            assertEquals(deploymentId, apiReg.getRegistryDeploymentId());
            assertEquals("Unauthenticated", apiReg.getOwner());
            assertEquals(reg.getOwner(), apiReg.getOwner());
            assertNotNull(apiReg.getCreatedAt());
            assertNotNull(apiReg.getUpdatedAt());
            assertEquals(reg.getHref(), apiReg.getHref());
            assertEquals(reg.getId(), apiReg.getId());
            assertEquals(reg.getKind(), apiReg.getKind());
            assertTrue(List.of("standard", "eval").contains(reg.getInstanceType().value()));
            // The status could've changed at this point
            if (apiReg.getStatus() == RegistryStatusValue.provisioning) {
                assertEquals(reg.getRegistryUrl() /* null */, apiReg.getRegistryUrl());
            } else {
                assertEquals(RegistryStatusValue.ready, apiReg.getStatus());
                assertTrue(apiReg.getRegistryUrl().startsWith(deployment.getRegistryDeploymentUrl()));
            }

            var list = given()
                    .when()
                    .queryParam("search", "name = " + reg.getName())
                    .get(BASE)
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(RegistryList.class);
            assertEquals(1, list.getTotal());
            assertNotNull(list.getItems().get(0));
            assertEquals(reg.getName(), list.getItems().get(0).getName());
            assertEquals(reg.getDescription(), list.getItems().get(0).getDescription());
            assertEquals(deploymentId, list.getItems().get(0).getRegistryDeploymentId());
            assertEquals(reg.getOwner(), list.getItems().get(0).getOwner());
            assertNotNull(list.getItems().get(0).getCreatedAt());
            assertNotNull(list.getItems().get(0).getUpdatedAt());
            assertEquals(reg.getHref(), list.getItems().get(0).getHref());
            assertEquals(reg.getId(), list.getItems().get(0).getId());
            assertEquals(reg.getKind(), list.getItems().get(0).getKind());
            assertEquals(reg.getInstanceType(), list.getItems().get(0).getInstanceType());
            // The status could've changed at this point
            if (list.getItems().get(0).getStatus() == RegistryStatusValue.provisioning) {
                assertEquals(reg.getRegistryUrl() /* null */, list.getItems().get(0).getRegistryUrl());
            } else {
                assertEquals(RegistryStatusValue.ready, list.getItems().get(0).getStatus());
                assertTrue(list.getItems().get(0).getRegistryUrl().startsWith(deployment.getRegistryDeploymentUrl()));
            }
        });

        regs = TestUtil.waitForReady(regs);

        // Delete
        regs.forEach(reg -> {
            given()
                    .when().delete(BASE + "/" + reg.getId())
                    .then().statusCode(HTTP_NO_CONTENT)
                    .log().all();
        });

        TestUtil.waitForDeletion(tms, TenantManagerConfig.builder()
                        .tenantManagerUrl(deployment.getTenantManagerUrl())
                        .registryDeploymentUrl(deployment.getRegistryDeploymentUrl()).build(),
                regs);

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