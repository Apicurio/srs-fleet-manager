package org.bf2.srs.fleetmanager.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;

@QuarkusTest
@TestProfile(AuthTestProfile.class)
public class SimpleAuthTest {

    public static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    @Test
    void testGetRegistries() {
        given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(HTTP_FORBIDDEN);
    }
}
