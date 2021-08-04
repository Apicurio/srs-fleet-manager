package org.bf2.srs.fleetmanager.other;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * This test is expected to be run only manually.
 * Keeping it in case it's needed in the future.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class MemoryProfilerTest {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    // TODO For some reason, @Ignore annotation does not work and the test is still executed. Commenting out for now.
    //@Test
    void createRegistry() {
        var d1 = new RegistryDeploymentCreateRest();
        d1.setName("a");
        d1.setTenantManagerUrl("https://tenant-manager");
        d1.setRegistryDeploymentUrl("https://registry");

        given().log().all()
                .when().contentType(ContentType.JSON).body(d1).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_OK);

        for (int i = 0; i < 200; i++) {

            var r = new RegistryCreateRest();
            r.setName("registry" + i);

            given().log().all()
                    .when().contentType(ContentType.JSON).body(r).post(BASE)
                    .then().statusCode(HTTP_OK);
        }

        //while (true) {
        //    TestUtil.delay(5000);
        //}
    }
}