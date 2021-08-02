package org.bf2.srs.fleetmanager.other;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class MemoryProfilerTest {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    @Ignore
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

        for (int i = 0; i < 200; i++) {

            var r1 = new RegistryCreateRest();
            r1.setName("registry" + i);

            given()
                    .log().all()
                    .when().contentType(ContentType.JSON).body(r1).post(BASE)
                    .then().statusCode(HTTP_OK)
                    .extract().as(RegistryRest.class).getId();
        }

        //while (true) {
        //    TestUtil.delay(5000);
        //}
    }
}