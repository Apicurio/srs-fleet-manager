package org.bf2.srs.fleetmanager.it;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class RegistryProvisioningIT extends SRSFleetManagerBaseIT {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    @Test
    void testProvisionRegistry() {
        var deployment = new RegistryDeploymentCreateRest();
        deployment.setName("test-deployment");
        deployment.setTenantManagerUrl(infra.getTenantManagerUri());
        deployment.setRegistryDeploymentUrl("http://registry-test");

        given()
            .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
            .then().statusCode(HTTP_FORBIDDEN)
            .log().all();

        var registry1 = new RegistryCreateRest();
        registry1.setName("test-registry-1");

        var registry1Result = given()
                    .when()
                        .contentType(ContentType.JSON)
                        .body(registry1)
                        .post(BASE)
                    .then().statusCode(HTTP_OK)
                    .log().all()
                    .extract().as(RegistryRest.class);

        assertNotEquals(RegistryStatusValueRest.failed, registry1Result.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
            .until(() -> {
                var reg = given()
                            .when().get(BASE + "/" + registry1Result.getId())
                            .then().statusCode(HTTP_OK)
                            .log().all()
                            .extract().as(RegistryRest.class);
                return reg.getStatus().equals(RegistryStatusValueRest.ready);
            });


        // Delete
        given()
            .when().delete(BASE + "/" + registry1Result.getId())
            .then().statusCode(HTTP_NO_CONTENT)
            .log().all();

    }

}